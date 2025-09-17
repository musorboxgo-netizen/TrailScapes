package org.trail.scapes.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.trail.scapes.domain.user.Review
import org.trail.scapes.dto.image.CreateImageDto
import org.trail.scapes.dto.user.review.CreateReviewDto
import org.trail.scapes.dto.user.review.ReviewDto
import org.trail.scapes.mappers.user.ReviewMapper
import org.trail.scapes.repositories.PlaceRepository
import org.trail.scapes.repositories.ReviewRepository
import org.trail.scapes.repositories.UserRepository
import java.time.LocalDateTime

@Service
open class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val reviewMapper: ReviewMapper,
    private val imageService: ImageService
) {
    @Transactional
    open fun create(dto: CreateReviewDto): ReviewDto {
        validateCreate(dto)

        // проверим, что автор и место существуют
        val author = userRepository.findById(dto.authorId)
            .orElseThrow { NoSuchElementException("User with id=${dto.authorId} not found") }
        val place = placeRepository.findById(dto.placeId)
            .orElseThrow { NoSuchElementException("Place with id=${dto.placeId} not found") }

        // создаём сущность; изображения НЕ трогаем здесь (делаем через imageService)
        val review = Review(
            id = null,
            rating = dto.rating,
            comment = dto.comment,
            author = author,
            place = place,
            createdAt = LocalDateTime.now()
        )
        val saved = reviewRepository.save(review)

        // если есть изображения — добавим их отдельными записями
        if (dto.images.isNotEmpty()) {
            dto.images.forEach { imgDto ->
                imageService.createForReview(saved.id!!, imgDto)
            }
        }

        // прочитаем с изображениями и отдадим DTO
        val reloaded = reviewRepository.findById(saved.id!!)
            .orElseThrow { IllegalStateException("Review was saved but cannot be reloaded") }

        return reviewMapper.toDto(reloaded)
    }

    /**
     * Обновить отзыв (только автор). Изменяем рейтинг/комментарий (изображения управляются отдельными методами).
     */
    @Transactional
    open fun update(reviewId: Long, currentUserId: Long, rating: Int?, comment: String?): ReviewDto {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review with id=$reviewId not found") }

        if (review.author.id != currentUserId) {
            throw IllegalStateException("Only author can update the review")
        }

        // допускаем «только рейтинг», «только текст», «оба»; но не оба null
        if (rating == null && comment == null) {
            throw IllegalArgumentException("Either rating or comment must be provided")
        }
        // если рейтинг есть — провалидируем
        rating?.let { ensureRatingInRange(it) }

        review.rating = rating
        review.comment = comment

        val saved = reviewRepository.save(review)
        return reviewMapper.toDto(saved)
    }

    /**
     * Добавить изображения к уже существующему отзыву (только автор).
     */
    @Transactional
    open fun addImages(reviewId: Long, currentUserId: Long, images: List<CreateImageDto>): ReviewDto {
        if (images.isEmpty()) return getById(reviewId)

        val review = reviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review with id=$reviewId not found") }

        if (review.author.id != currentUserId) {
            throw IllegalStateException("Only author can add images")
        }

        images.forEach { imageService.createForReview(reviewId, it) }

        val reloaded = reviewRepository.findById(reviewId)
            .orElseThrow { IllegalStateException("Review not found after adding images") }

        return reviewMapper.toDto(reloaded)
    }

    /**
     * Удалить одно изображение отзыва (только автор).
     */
    @Transactional
    open fun removeImage(reviewId: Long, imageId: Long, currentUserId: Long): ReviewDto {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review with id=$reviewId not found") }

        if (review.author.id != currentUserId) {
            throw IllegalStateException("Only author can remove images")
        }

        // imageService сам удалит запись и файл в облаке
        imageService.deleteById(imageId)

        val reloaded = reviewRepository.findById(reviewId)
            .orElseThrow { IllegalStateException("Review not found after image removal") }

        return reviewMapper.toDto(reloaded)
    }

    /**
     * Удалить отзыв (только автор).
     * Перед удалением удаляем все связанные изображения через ImageService,
     * чтобы убрать их и из Cloudinary.
     */
    @Transactional
    open fun delete(reviewId: Long, currentUserId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { NoSuchElementException("Review with id=$reviewId not found") }

        if (review.author.id != currentUserId) {
            throw IllegalStateException("Only author can delete the review")
        }

        // удаляем картинки через сервис (иначе файлы останутся в облаке)
        review.images.forEach { img ->
            imageService.deleteById(img.id!!)
        }

        reviewRepository.delete(review)
    }

    /**
     * Получить отзывы места (по умолчанию сортируем по дате – новые сначала).
     * Для больших объёмов сделай перегрузку с Pageable.
     */
    @Transactional(readOnly = true)
    open fun getByPlace(placeId: Long): List<ReviewDto> =
        reviewRepository.findAllByPlaceIdOrderByCreatedAtDesc(placeId)
            .map { reviewMapper.toDto(it) }

    /**
     * Получить отзывы пользователя (написанные им).
     */
    @Transactional(readOnly = true)
    open fun getByUser(userId: Long): List<ReviewDto> =
        reviewRepository.findAllByAuthorIdOrderByCreatedAtDesc(userId)
            .map { reviewMapper.toDto(it) }

    /**
     * Получить один отзыв по id.
     */
    @Transactional(readOnly = true)
    open fun getById(reviewId: Long): ReviewDto =
        reviewRepository.findById(reviewId)
            .map { reviewMapper.toDto(it) }
            .orElseThrow { NoSuchElementException("Review with id=$reviewId not found") }

    /**
     * Средний рейтинг места (null если ещё не оценивали).
     * Можно брать из PlaceRepository.averageRatingByPlaceId, чтобы не дублировать агрегацию.
     */
    @Transactional(readOnly = true)
    open fun averageRatingForPlace(placeId: Long): Double? =
        placeRepository.averageRatingByPlaceId(placeId)

    // --- helpers ---

    private fun validateCreate(dto: CreateReviewDto) {
        if (dto.rating == null && dto.comment == null) {
            throw IllegalArgumentException("Either rating or comment must be provided")
        }
        dto.rating?.let { ensureRatingInRange(it) }
        // comment длиной до 2000 символов уже ограничен на уровне @Column(length=2000)
    }

    private fun ensureRatingInRange(r: Int) {
        if (r !in 1..5) throw IllegalArgumentException("Rating must be in range 1..5")
    }
}
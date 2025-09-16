package org.trail.scapes.services

import com.cloudinary.Cloudinary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.trail.scapes.domain.Image
import org.trail.scapes.dto.image.CreateImageDto
import org.trail.scapes.dto.image.ImageDto
import org.trail.scapes.mappers.image.ImageMapper
import org.trail.scapes.repositories.ImageRepository
import org.trail.scapes.repositories.PlaceRepository
import org.trail.scapes.repositories.ReviewRepository
import org.trail.scapes.repositories.UserRepository
import org.trail.scapes.util.CloudinaryUtils
import java.time.LocalDateTime

@Service
open class ImageService(
    private val cloudinary: Cloudinary,
    private val imageRepository: ImageRepository,
    private val placeRepository: PlaceRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val imageMapper: ImageMapper
) {

    @Transactional(readOnly = true)
    open fun getById(id: Long): ImageDto = imageMapper.toDto(findEntity(id))

    @Transactional(readOnly = true)
    open fun getUrlById(id: Long): String? = imageRepository.findUrlById(id)

    @Transactional
    open fun create(dto: CreateImageDto): ImageDto {
        val entity = imageMapper.fromCreateDto(dto)
        val saved = imageRepository.save(entity)
        return imageMapper.toDto(saved)
    }

    @Transactional
    open fun deleteById(id: Long) {
        val image = findEntity(id)

        image.user?.let { user ->
            if (user.profileImage?.id == id) {
                user.profileImage = null
            }
        }

        deleteFromCloudinary(image.publicId)

        imageRepository.delete(image)
    }

    @Transactional
    open fun updateAltText(id: Long, altText: String?): ImageDto {
        val entity = findEntity(id)
        entity.altText = altText
        return imageMapper.toDto(imageRepository.save(entity))
    }

    @Transactional
    open fun createForPlace(placeId: Long, dto: CreateImageDto): ImageDto {
        val placeRef = placeRepository.getReferenceById(placeId)
        val entity = Image(
            id = null,
            url = dto.url,
            publicId = dto.publicId,
            altText = dto.altText,
            uploadedAt = LocalDateTime.now(),
            place = placeRef,
            review = null,
            user = null
        )
        return imageMapper.toDto(imageRepository.save(entity))
    }

    @Transactional
    open fun createForReview(reviewId: Long, dto: CreateImageDto): ImageDto {
        val reviewRef = reviewRepository.getReferenceById(reviewId)
        val entity = Image(
            id = null,
            url = dto.url,
            publicId = dto.publicId,
            altText = dto.altText,
            uploadedAt = LocalDateTime.now(),
            place = null,
            review = reviewRef,
            user = null
        )
        return imageMapper.toDto(imageRepository.save(entity))
    }

    @Transactional
    open fun setUserProfileImage(userId: Long, dto: CreateImageDto): ImageDto {
        val user = userRepository.findById(userId).orElseThrow()

        user.profileImage?.let { existing ->
            deleteFromCloudinary(existing.publicId)
            imageRepository.delete(existing)
            user.profileImage = null
        }

        val entity = Image(
            id = null,
            url = dto.url,
            publicId = dto.publicId,
            altText = dto.altText,
            uploadedAt = LocalDateTime.now(),
            place = null,
            review = null,
            user = user
        )
        val saved = imageRepository.save(entity)
        user.profileImage = saved
        return imageMapper.toDto(saved)
    }

    @Transactional
    open fun reassignToPlace(imageId: Long, newPlaceId: Long) {
        val image = findEntity(imageId)
        image.place = placeRepository.getReferenceById(newPlaceId)
        image.review = null
        image.user = null
        imageRepository.save(image)
    }

    private fun deleteFromCloudinary(publicId: String?) {
        if (publicId.isNullOrBlank()) return
        try {
            cloudinary.uploader().destroy(publicId, mapOf("resource_type" to "image"))
        } catch (_: Exception) {
            // Логируй при необходимости, но не рвём транзакцию (best-effort)
        }
    }

    private fun findEntity(id: Long): Image =
        imageRepository.findById(id).orElseThrow { IllegalArgumentException("Image $id not found") }
}
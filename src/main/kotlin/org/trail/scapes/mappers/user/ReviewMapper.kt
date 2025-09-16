package org.trail.scapes.mappers.user

import org.springframework.stereotype.Service
import org.trail.scapes.domain.Place
import org.trail.scapes.domain.user.Review
import org.trail.scapes.domain.user.User
import org.trail.scapes.dto.user.review.CreateReviewDto
import org.trail.scapes.dto.user.review.ReviewDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import org.trail.scapes.mappers.image.ImageMapper

@Service
class ReviewMapper(
    private val imageMapper: ImageMapper
): ExtendedEntityMapper<Review, ReviewDto, CreateReviewDto> {

    override fun toDto(entity: Review): ReviewDto =
        ReviewDto(
            id        = entity.id,
            rating    = entity.rating,
            comment   = entity.comment,
            userId    = entity.author.id,
            placeId   = entity.place.id,
            createdAt = entity.createdAt,
            images    = entity.images.map { imageMapper.toDto(it) }
        )

    override fun toEntity(dto: ReviewDto): Review {
        val review = Review(
            id = dto.id,
            rating = dto.rating,
            comment = dto.comment,
            author = User().apply { id = dto.userId },
            place = Place().apply { id = dto.placeId },
            createdAt = dto.createdAt
        )

        review.images = dto.images
            .map { imageMapper.toEntity(it).apply { this.review = review } }
            .toMutableList()

        return review
    }

    override fun fromCreateDto(dto: CreateReviewDto): Review {
        val review = Review(
            id        = null,
            rating    = dto.rating,
            comment   = dto.comment,
            author    = User().apply { id = dto.authorId },
            place     = Place().apply { id = dto.placeId }
        )

        review.images = dto.images
            .map { imageMapper.fromCreateDto(it).apply { this.review = review } }
            .toMutableList()

        return review
    }
}
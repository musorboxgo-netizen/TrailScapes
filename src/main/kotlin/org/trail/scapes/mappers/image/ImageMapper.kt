package org.trail.scapes.mappers.image

import org.springframework.stereotype.Service
import org.trail.scapes.domain.Image
import org.trail.scapes.dto.image.CreateImageDto
import org.trail.scapes.dto.image.ImageDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import java.time.LocalDateTime

@Service
class ImageMapper : ExtendedEntityMapper<Image, ImageDto, CreateImageDto> {
    override fun toDto(entity: Image): ImageDto =
        ImageDto(
            id = entity.id,
            url = entity.url,
            publicId = entity.publicId,
            altText = entity.altText,
            uploadedAt = entity.uploadedAt,
            placeId = entity.place?.id,
            reviewId = entity.review?.id,
            userId = entity.user?.id
        )

    override fun toEntity(dto: ImageDto): Image =
        Image(
            id = dto.id,
            url = dto.url,
            publicId = dto.publicId,
            altText = dto.altText,
            uploadedAt = dto.uploadedAt,
            place = null,
            review = null,
            user = null
        )

    override fun fromCreateDto(dto: CreateImageDto): Image =
        Image(
            id = null,
            url = dto.url,
            publicId = dto.publicId,
            altText = dto.altText,
            uploadedAt = LocalDateTime.now(),
            place = null,
            review = null,
            user = null
        )
}
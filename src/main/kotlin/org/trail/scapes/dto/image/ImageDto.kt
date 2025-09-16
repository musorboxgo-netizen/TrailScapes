package org.trail.scapes.dto.image

import java.time.LocalDateTime

data class ImageDto(
    val id: Long?,
    val url: String,
    val publicId: String,
    val altText: String?,
    val uploadedAt: LocalDateTime,
    val placeId: Long?,
    val reviewId: Long?,
    val userId: Long?
)
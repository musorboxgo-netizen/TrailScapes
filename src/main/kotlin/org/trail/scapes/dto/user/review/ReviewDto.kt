package org.trail.scapes.dto.user.review

import org.trail.scapes.dto.image.ImageDto
import java.time.LocalDateTime

data class ReviewDto(
    val id: Long?,
    val rating: Int?,
    val comment: String?,
    val userId: Long?,
    val placeId: Long?,
    val createdAt: LocalDateTime,
    val images: List<ImageDto>
)

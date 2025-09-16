package org.trail.scapes.dto.user.review

import org.trail.scapes.dto.image.CreateImageDto

data class CreateReviewDto(
    val rating: Int?,
    val comment: String?,
    val authorId: Long,
    val placeId: Long,
    val images: List<CreateImageDto> = emptyList()
)
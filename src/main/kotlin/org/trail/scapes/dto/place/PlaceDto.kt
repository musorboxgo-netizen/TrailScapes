package org.trail.scapes.dto.place

import org.trail.scapes.dto.category.CategoryDto
import org.trail.scapes.dto.image.ImageDto
import org.trail.scapes.dto.user.review.ReviewDto
import java.util.Collections.emptyList

data class PlaceDto(
    val id: Long?,
    val title: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val createdByUserId: Long?,
    val categories: List<CategoryDto> = emptyList(),

    val images: List<ImageDto> = emptyList(),
    val reviews: List<ReviewDto> = emptyList(),

    val imagesCount: Int? = null,
    val reviewsCount: Int? = null,
    val averageRating: Double? = null
)
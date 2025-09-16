package org.trail.scapes.dto.place

import org.trail.scapes.dto.image.CreateImageDto

data class CreatePlaceDto(
    val title: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val createdByUserId: Long,
    val categoryIds: List<Long> = emptyList(),
    val images: List<CreateImageDto> = emptyList()
)
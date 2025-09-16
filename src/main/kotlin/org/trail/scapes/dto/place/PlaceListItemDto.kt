package org.trail.scapes.dto.place

data class PlaceListItemDto(
    val id: Long?,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val thumbnailUrl: String? = null
)
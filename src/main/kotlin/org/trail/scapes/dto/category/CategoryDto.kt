package org.trail.scapes.dto.category

data class CategoryDto(
    val id: Long?,
    val name: String,
    val description: String?,
    val placesAssigned: Int?
)
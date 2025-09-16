package org.trail.scapes.dto.category

data class CreateCategoryDto (
    val name: String,
    val description: String? = null
)
package org.trail.scapes.dto.image

data class CreateImageDto(
    val url: String,
    val publicId: String,
    val altText: String? = null
)
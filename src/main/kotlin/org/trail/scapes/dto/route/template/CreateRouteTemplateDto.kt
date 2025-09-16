package org.trail.scapes.dto.route.template

data class CreateRouteTemplateDto(
    val name: String,
    val description: String? = null,
    val places: List<CreateRouteTemplatePlaceDto> = emptyList()
)
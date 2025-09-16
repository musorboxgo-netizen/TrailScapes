package org.trail.scapes.dto.route.template

import java.util.Collections.emptyList


data class RouteTemplateDto(
    val id: Long?,
    val name: String,
    val description: String?,
    val places: List<RouteTemplatePlaceDto> = emptyList(),
    val placesCount: Int? = null
)
package org.trail.scapes.dto.route.template

import org.trail.scapes.dto.place.PlaceListItemDto

data class RouteTemplatePlaceDto(
    val templateId: Long,
    val placeId: Long,
    val position: Int,
    val place: PlaceListItemDto? = null
)
package org.trail.scapes.dto.route.place

import org.trail.scapes.dto.place.PlaceListItemDto

data class RoutePlaceDto(
    val routeId: Long,
    val placeId: Long,
    val position: Int,

    val place: PlaceListItemDto? = null
)
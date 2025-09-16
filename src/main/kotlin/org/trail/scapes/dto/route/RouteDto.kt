package org.trail.scapes.dto.route

import org.trail.scapes.dto.route.participant.RouteParticipantDto
import org.trail.scapes.dto.route.place.RoutePlaceDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Collections.emptyList

data class RouteDto(
    val id: Long?,
    val templateId: Long?,
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    val description: String?,
    val initiatorId: Long,
    val expiresAt: LocalDateTime?,

    val routePlaces: List<RoutePlaceDto> = emptyList(),
    val participants: List<RouteParticipantDto> = emptyList(),

    val routePlacesCount: Int? = null,
    val participantsCount: Int? = null
)

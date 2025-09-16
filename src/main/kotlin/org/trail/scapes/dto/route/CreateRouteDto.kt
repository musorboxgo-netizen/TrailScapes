package org.trail.scapes.dto.route

import org.trail.scapes.dto.route.participant.CreateRouteParticipantDto
import org.trail.scapes.dto.route.place.CreateRoutePlaceDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class CreateRouteDto(
    val templateId: Long? = null,
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    val description: String? = null,
    val expiresAt: LocalDateTime? = null,

    val places: List<CreateRoutePlaceDto>,

    val participants: List<CreateRouteParticipantDto> = emptyList()
)
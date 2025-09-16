package org.trail.scapes.dto.route.participant

import org.trail.scapes.domain.enums.ParticipantRole

data class CreateRouteParticipantDto(
    val userId: Long,
    val role: ParticipantRole = ParticipantRole.PARTICIPANT
)
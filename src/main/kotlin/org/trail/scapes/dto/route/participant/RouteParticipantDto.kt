package org.trail.scapes.dto.route.participant

import org.trail.scapes.domain.enums.ParticipantRole
import org.trail.scapes.dto.user.UserListItemDto
import java.time.LocalDateTime

data class RouteParticipantDto(
    val id: Long?,
    val userId: Long,
    val routeId: Long,
    val role: ParticipantRole,
    val joinedAt: LocalDateTime,

    val user: UserListItemDto? = null
)
package org.trail.scapes.dto.user

import java.time.LocalDateTime

data class LikeDto(
    val id: Long?,
    val userId: Long,
    val reviewId: Long,
    val createdAt: LocalDateTime
)

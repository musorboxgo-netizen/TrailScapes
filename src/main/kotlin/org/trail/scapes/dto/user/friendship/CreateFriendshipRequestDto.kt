package org.trail.scapes.dto.user.friendship

data class CreateFriendshipRequestDto(
    val addresseeId: Long,
    val message: String? = null
)
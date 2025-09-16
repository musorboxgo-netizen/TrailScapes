package org.trail.scapes.dto.user.friendship

import org.trail.scapes.domain.enums.FriendshipRequestStatus
import org.trail.scapes.dto.user.UserListItemDto
import java.time.LocalDateTime

data class FriendshipRequestDto(
    val id: Long?,
    val requesterId: Long,
    val addresseeId: Long,
    val status: FriendshipRequestStatus,
    val sentAt: LocalDateTime,
    val message: String? = null,

    val requester: UserListItemDto? = null,
    val addressee: UserListItemDto? = null
)
package org.trail.scapes.domain.notification

import jakarta.persistence.*
import org.trail.scapes.domain.user.friendship.FriendshipRequest
import org.trail.scapes.domain.user.User
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("FRIEND_REQUEST")
@Table(name = "friend_request_notifications")
@PrimaryKeyJoinColumn(name = "notification_id")
open class FriendRequestNotification(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_request_id", nullable = false)
    open var friendshipRequest: FriendshipRequest,

    id: Long? = null,
    user: User = User(),
    createdAt: LocalDateTime = LocalDateTime.now(),
    isRead: Boolean = false
) : Notification(id, user, createdAt, isRead) {
    constructor() : this(
        friendshipRequest = FriendshipRequest(),
        id            = null,
        user          = User(),
        createdAt     = LocalDateTime.now(),
        isRead        = false
    )
}
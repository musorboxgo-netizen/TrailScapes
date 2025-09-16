package org.trail.scapes.domain.notification

import jakarta.persistence.*
import org.trail.scapes.domain.user.User
import org.trail.scapes.domain.route.RouteInvitation
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("ROUTE_INVITATION")
@Table(name = "route_invitation_notifications")
@PrimaryKeyJoinColumn(name = "notification_id")
open class RouteInvitationNotification(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invitation_id", nullable = false)
    open var invitation: RouteInvitation,

    id: Long? = null,
    user: User = User(),
    createdAt: LocalDateTime = LocalDateTime.now(),
    isRead: Boolean = false
) : Notification(id, user, createdAt, isRead) {
    constructor() : this(RouteInvitation(), null, User(), LocalDateTime.now(), false)
}
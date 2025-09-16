package org.trail.scapes.domain.notification

import jakarta.persistence.*
import org.trail.scapes.domain.user.User
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("INFO")
@Table(name = "info_notifications")
@PrimaryKeyJoinColumn(name = "notification_id")
open class InfoNotification(

    @Column(nullable = false, length = 1000)
    open var message: String,

    id: Long? = null,
    user: User = User(),
    createdAt: LocalDateTime = LocalDateTime.now(),
    isRead: Boolean = false
) : Notification(id, user, createdAt, isRead) {
    constructor() : this("message", null, User(), LocalDateTime.now(),false)
}
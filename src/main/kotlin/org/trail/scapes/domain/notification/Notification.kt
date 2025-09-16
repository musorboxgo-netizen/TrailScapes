package org.trail.scapes.domain.notification

import jakarta.persistence.*
import org.trail.scapes.domain.user.User
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "notification_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "notifications")
open class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var recipient: User,

    @Column(name = "created_at", nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_read", nullable = false)
    open var isRead: Boolean = false
) {
    constructor() : this(null, User(), LocalDateTime.now(), false)
}
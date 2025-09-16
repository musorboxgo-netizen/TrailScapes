package org.trail.scapes.domain.user.friendship

import jakarta.persistence.*
import org.trail.scapes.domain.enums.FriendshipStatus
import org.trail.scapes.domain.user.User
import java.time.LocalDateTime

@Entity
@Table(
    name = "friendships",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_friendship_users_low_high",
            columnNames = ["user_low_id", "user_high_id"]
        )
    ],
    indexes = [
        Index(name = "idx_friendship_low",  columnList = "user_low_id"),
        Index(name = "idx_friendship_high", columnList = "user_high_id"),
        Index(name = "idx_friendship_status", columnList = "status")
    ]
)
open class Friendship(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_low_id", nullable = false)
    open var userLow: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_high_id", nullable = false)
    open var userHigh: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: FriendshipStatus = FriendshipStatus.ACTIVE,

    @Column(name = "since", nullable = false)
    open var since: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_by_user_id")
    open var createdByUserId: Long? = null
) {
    constructor(): this(null, User(), User(), FriendshipStatus.ACTIVE, LocalDateTime.now(), null)
}
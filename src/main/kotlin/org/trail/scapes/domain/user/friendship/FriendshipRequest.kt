package org.trail.scapes.domain.user.friendship

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.enums.FriendshipRequestStatus
import org.trail.scapes.domain.user.User
import java.time.LocalDateTime

@Entity
@Table(
    name = "friend_requests",
    uniqueConstraints = [UniqueConstraint(columnNames = ["requester_id", "addressee_id"])]
)
open class FriendshipRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnore
    open var requester: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "addressee_id", nullable = false)
    @JsonIgnore
    open var addressee: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: FriendshipRequestStatus = FriendshipRequestStatus.PENDING,

    @Column(name = "sent_at", nullable = false)
    open var sentAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "message")
    open var message: String? = null,
) {
    constructor() : this(null, User(), User(), FriendshipRequestStatus.PENDING, LocalDateTime.now())
}
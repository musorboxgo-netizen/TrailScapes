package org.trail.scapes.domain.route

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.user.User
import org.trail.scapes.domain.enums.RequestStatus
import java.time.LocalDateTime

@Entity
@Table(
    name = "route_invitations",
    uniqueConstraints = [UniqueConstraint(columnNames = ["route_id", "invitee_id"])]
)
open class RouteInvitation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnore
    open var route: Route,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inviter_id", nullable = false)
    @JsonIgnore
    open var inviter: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invitee_id", nullable = false)
    @JsonIgnore
    open var invitee: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: RequestStatus = RequestStatus.PENDING,

    @Column(name = "sent_at", nullable = false)
    open var sentAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "responded_at")
    open var respondedAt: LocalDateTime? = null
) {
    constructor() : this(null, Route(), User(), User(), RequestStatus.PENDING, LocalDateTime.now(), null)
}
package org.trail.scapes.domain.route

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.user.User
import org.trail.scapes.domain.enums.ParticipantRole
import java.time.LocalDateTime

@Entity
@Table(name = "route_participants")
open class RouteParticipant(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    open var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnore
    open var route: Route,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var role: ParticipantRole = ParticipantRole.PARTICIPANT,

    @Column(name = "joined_at", nullable = false)
    open var joinedAt: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(null, User(), Route(), ParticipantRole.INITIATOR, LocalDateTime.now())
}

package org.trail.scapes.domain.route

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.user.User
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "routes")
open class Route(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "template_id")
    open var template: RouteTemplate? = null,

    @Column(nullable = false)
    open var name: String,

    @Column(nullable = false)
    open var date: LocalDate,

    @Column(nullable = false)
    open var time: LocalTime,

    open var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "initiator_id", nullable = false)
    @JsonIgnore
    open var initiator: User,

    @Column(name = "expires_at", nullable = true)
    open var expiresAt: LocalDateTime? = null
) {
    constructor() : this(
        null, null, "", LocalDate.now(), LocalTime.MIDNIGHT,
        null, User(), null
    )

    @OneToMany(
        mappedBy = "route",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("position ASC")
    @JsonIgnore
    open var routePlaces: MutableList<RoutePlace> = mutableListOf()

    @OneToMany(
        mappedBy = "route",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonIgnore
    open var participants: MutableList<RouteParticipant> = mutableListOf()
}
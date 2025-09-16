package org.trail.scapes.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.Image
import org.trail.scapes.domain.Place
import org.trail.scapes.domain.notification.Notification
import org.trail.scapes.domain.route.RouteInvitation
import org.trail.scapes.domain.route.RouteParticipant
import java.time.LocalDate

@Entity
@Table(name = "users")
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false, unique = true)
    open var username: String,

    @Column(nullable = false, unique = true)
    open var email: String,

    @Column(nullable = false)
    open var firstName: String,

    @Column(nullable = false)
    open var lastName: String,

    @Column(nullable = false)
    open var dateOfBirth: LocalDate,

    @Column(nullable = false)
    @JsonIgnore
    open var password: String,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "profile_image_id")
    open var profileImage: Image? = null
) {
    constructor() : this(
        null, "username", "email", "name",
        "surname", LocalDate.of(1800, 12, 31), "password", null
    )

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_places",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "place_id")]
    )
    @JsonIgnore
    open var favoritePlaces: MutableList<Place> = mutableListOf()

    @OneToMany(
        mappedBy = "createdBy",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @JsonIgnore
    open var usersAddedPlaces: MutableList<Place> = mutableListOf()

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @JsonIgnore
    open var reviews: MutableList<Review> = mutableListOf()
}

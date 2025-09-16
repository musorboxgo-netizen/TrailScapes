package org.trail.scapes.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.user.Review
import org.trail.scapes.domain.user.User

@Entity
@Table(
    name = "places",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_place_title_lat_lng",
            columnNames = ["title", "latitude", "longitude"]
        )
    ]
)
open class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false)
    open var title: String,

    @Column(nullable = false)
    open var description: String,

    @Column(nullable = false)
    open var address: String,

    @Column(nullable = false)
    open var latitude: Double = -1.0,

    @Column(nullable = false)
    open var longitude: Double = -1.0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    open var createdBy: User
) {
    constructor() : this(null, "title", "description", "address", -1.0, -1.0, User())

    @OneToMany(
        mappedBy = "place",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @JsonIgnore
    open var reviews: MutableList<Review> = mutableListOf()

    @OneToMany(
        mappedBy = "place",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @JsonIgnore
    open var images: MutableList<Image> = mutableListOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "place_categories",
        joinColumns = [JoinColumn(name = "place_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    open var categories: MutableList<Category> = mutableListOf()
}

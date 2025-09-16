package org.trail.scapes.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.Image
import org.trail.scapes.domain.Place
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
open class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column
    open var rating: Int? = null,

    @Column(length = 2000)
    open var comment: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    open var author: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    open var place: Place,

    @Column(nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now(),

    ) {
    constructor() : this(null, null, null, User(), Place(), LocalDateTime.now())

    @OneToMany(
        mappedBy = "review",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @JsonIgnore
    open var images: MutableList<Image> = mutableListOf()
}
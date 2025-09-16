package org.trail.scapes.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
open class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    open var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    @JsonIgnore
    open var review: Review,

    @Column(nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(null, User(), Review(), LocalDateTime.now())
}

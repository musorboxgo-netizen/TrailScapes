package org.trail.scapes.domain

import jakarta.persistence.*
import org.hibernate.annotations.Check
import org.trail.scapes.domain.user.Review
import org.trail.scapes.domain.user.User
import java.time.LocalDateTime

@Entity
@Check(
    constraints = """
    ((place_id   IS NOT NULL)::int
   + (review_id  IS NOT NULL)::int
   + (user_id    IS NOT NULL)::int) = 1
  """
)
@Table(name = "images",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id"])])
open class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false)
    open var url: String,

    @Column(name = "public_id", nullable = false)
    open var publicId: String,

    @Column(name = "alt_text", length = 500)
    open var altText: String? = null,

    @Column(name = "uploaded_at", nullable = false)
    open var uploadedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = true)
    open var place: Place? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = true)
    open var review: Review? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true, unique = true)
    open var user: User? = null
) {
    constructor() : this(null, "url228", "id",null, LocalDateTime.now(), null, null)
}

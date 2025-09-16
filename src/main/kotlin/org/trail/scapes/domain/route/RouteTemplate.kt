package org.trail.scapes.domain.route

import jakarta.persistence.*

@Entity
@Table(name = "route_templates")
open class RouteTemplate(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false)
    open var name: String,

    open var description: String? = null
) {
    constructor() : this(null, "", null)

    @OneToMany(
        mappedBy = "template",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    open var templateRoutePlaces: MutableList<RouteTemplatePlace> = mutableListOf()
}
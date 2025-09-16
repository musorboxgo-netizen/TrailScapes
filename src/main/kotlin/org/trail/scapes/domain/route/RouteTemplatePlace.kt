package org.trail.scapes.domain.route

import jakarta.persistence.*
import org.trail.scapes.domain.Place

@Embeddable
data class RouteTemplatePlaceId(
    @Column(name = "template_id") var templateId: Long? = null,
    @Column(name = "place_id")    var placeId:    Long? = null
)

@Entity
@Table(name = "template_route_places")
open class RouteTemplatePlace(
    @EmbeddedId
    open var id: RouteTemplatePlaceId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("templateId")
    @JoinColumn(name = "template_id")
    open var template: RouteTemplate,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("placeId")
    @JoinColumn(name = "place_id")
    open var place: Place,

    @Column(nullable = false)
    open var position: Int
) {
    constructor() : this(RouteTemplatePlaceId(), RouteTemplate(), Place(), 0)
}
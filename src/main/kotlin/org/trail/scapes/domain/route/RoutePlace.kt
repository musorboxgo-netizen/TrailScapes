package org.trail.scapes.domain.route

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.trail.scapes.domain.Place

@Embeddable
data class RoutePlaceId(
    @Column(name = "route_id") var routeId: Long? = null,
    @Column(name = "place_id") var placeId: Long? = null
)

@Entity
@Table(name = "route_places")
open class RoutePlace(

    @EmbeddedId
    open var id: RoutePlaceId,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnore
    open var route: Route,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    open var place: Place,

    @Column(nullable = false)
    open var position: Int
) {
    constructor(): this(RoutePlaceId(), Route(), Place(), 0)
}
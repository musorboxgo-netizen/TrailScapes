package org.trail.scapes.mappers.route

import org.springframework.stereotype.Service
import org.trail.scapes.domain.Place
import org.trail.scapes.domain.route.*
import org.trail.scapes.domain.user.User
import org.trail.scapes.dto.place.PlaceListItemDto
import org.trail.scapes.dto.route.CreateRouteDto
import org.trail.scapes.dto.route.RouteDto
import org.trail.scapes.dto.route.participant.RouteParticipantDto
import org.trail.scapes.dto.route.place.RoutePlaceDto
import org.trail.scapes.dto.user.UserListItemDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import org.trail.scapes.mappers.place.PlaceMapper
import org.trail.scapes.mappers.user.UserMapper
import java.time.LocalDateTime

@Service
class RouteMapper(
    private val userMapper: UserMapper,
    private val placeMapper: PlaceMapper
) : ExtendedEntityMapper<Route, RouteDto, CreateRouteDto> {

    override fun toDto(entity: Route): RouteDto =
        RouteDto(
            id = entity.id,
            templateId = entity.template?.id,
            name = entity.name,
            date = entity.date,
            time = entity.time,
            description = entity.description,
            initiatorId = entity.initiator.id!!,
            expiresAt = entity.expiresAt,

            routePlaces = entity.routePlaces.map {
                RoutePlaceDto(
                    routeId = it.route.id!!,
                    placeId = it.place.id!!,
                    position = it.position,
                    place = placeMapper.toListItem(it.place)
                )
            },
            participants = entity.participants.map {
                RouteParticipantDto(
                    id = it.id,
                    userId = it.user.id!!,
                    routeId = it.route.id!!,
                    role = it.role,
                    joinedAt = it.joinedAt,
                    user = userMapper.toListItem(it.user)
                )
            },

            routePlacesCount = entity.routePlaces.size,
            participantsCount = entity.participants.size
        )

    override fun toEntity(dto: RouteDto): Route =
        Route(
            id = dto.id,
            template = dto.templateId?.let { RouteTemplate().apply { id = it } },
            name = dto.name,
            date = dto.date,
            time = dto.time,
            description = dto.description,
            initiator = User().apply { id = dto.initiatorId },
            expiresAt = dto.expiresAt
        ).also { r ->
            r.routePlaces = dto.routePlaces.map {
                RoutePlace(
                    id = RoutePlaceId(routeId = dto.id, placeId = it.placeId),
                    route = r,
                    place = Place().apply { id = it.placeId },
                    position = it.position
                )
            }.toMutableList()

            r.participants = dto.participants.map {
                RouteParticipant(
                    id = it.id,
                    user = User().apply { id = it.userId },
                    route = r,
                    role = it.role,
                    joinedAt = it.joinedAt
                )
            }.toMutableList()
        }

    override fun fromCreateDto(dto: CreateRouteDto): Route =
        Route(
            id = null,
            template = dto.templateId?.let { RouteTemplate().apply { id = it } },
            name = dto.name,
            date = dto.date,
            time = dto.time,
            description = dto.description,
            initiator = User().apply { id = null },
            expiresAt = dto.expiresAt
        ).also { r ->
            r.routePlaces = dto.places
                .sortedBy { it.position }
                .map {
                    RoutePlace(
                        id = RoutePlaceId(routeId = null, placeId = it.placeId),
                        route = r,
                        place = Place().apply { id = it.placeId },
                        position = it.position
                    )
                }.toMutableList()

            r.participants = dto.participants.map {
                RouteParticipant(
                    id = null,
                    user = User().apply { id = it.userId },
                    route = r,
                    role = it.role,
                    joinedAt = LocalDateTime.now()
                )
            }.toMutableList()
        }

    fun toListItem(entity: Place): PlaceListItemDto =
        PlaceListItemDto(
            id = entity.id,
            title = entity.title,
            latitude = entity.latitude,
            longitude = entity.longitude,
            thumbnailUrl = entity.images.firstOrNull()?.url
        )
}
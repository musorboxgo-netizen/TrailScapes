package org.trail.scapes.mappers.route

import org.trail.scapes.domain.Place
import org.trail.scapes.domain.route.RouteTemplate
import org.trail.scapes.dto.route.template.CreateRouteTemplateDto
import org.trail.scapes.dto.route.template.RouteTemplateDto
import org.trail.scapes.domain.route.RouteTemplatePlace
import org.trail.scapes.domain.route.RouteTemplatePlaceId
import org.trail.scapes.dto.route.template.RouteTemplatePlaceDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import org.trail.scapes.mappers.place.PlaceMapper

class RouteTemplateMapper(
    private val placeMapper: PlaceMapper
) : ExtendedEntityMapper<RouteTemplate, RouteTemplateDto, CreateRouteTemplateDto> {

    override fun toDto(entity: RouteTemplate): RouteTemplateDto =
        RouteTemplateDto(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            places = entity.templateRoutePlaces.map { trp ->
                RouteTemplatePlaceDto(
                    templateId = trp.template.id!!,
                    placeId = trp.place.id!!,
                    position = trp.position,
                    place = placeMapper.toListItem(trp.place)
                )
            },
            placesCount = entity.templateRoutePlaces.size
        )

    override fun toEntity(dto: RouteTemplateDto): RouteTemplate =
        RouteTemplate(
            id = dto.id,
            name = dto.name,
            description = dto.description
        ).also { template ->
            template.templateRoutePlaces = dto.places.map { dtoPlace ->
                RouteTemplatePlace(
                    id = RouteTemplatePlaceId(dtoPlace.templateId, dtoPlace.placeId),
                    template = template,
                    place = Place().apply { id = dtoPlace.placeId },
                    position = dtoPlace.position
                )
            }.toMutableList()
        }

    override fun fromCreateDto(dto: CreateRouteTemplateDto): RouteTemplate =
        RouteTemplate(
            id = null,
            name = dto.name,
            description = dto.description
        ).also { template ->
            template.templateRoutePlaces = dto.places.map { createDto ->
                RouteTemplatePlace(
                    id = RouteTemplatePlaceId(null, createDto.placeId),
                    template = template,
                    place = Place().apply { id = createDto.placeId },
                    position = createDto.position
                )
            }.toMutableList()
        }
}
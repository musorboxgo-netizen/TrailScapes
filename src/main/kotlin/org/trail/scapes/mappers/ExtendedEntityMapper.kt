package org.trail.scapes.mappers

interface ExtendedEntityMapper<Entity, Dto, CreateDto>: EntityMapper<Entity, Dto> {
    fun fromCreateDto(dto: CreateDto): Entity
}
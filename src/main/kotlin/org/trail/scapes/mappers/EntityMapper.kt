package org.trail.scapes.mappers

interface EntityMapper<Entity, Dto> {
    fun toEntity(dto: Dto): Entity
    fun toDto(entity: Entity): Dto
}
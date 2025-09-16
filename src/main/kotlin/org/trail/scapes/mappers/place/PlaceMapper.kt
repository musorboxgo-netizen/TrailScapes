package org.trail.scapes.mappers.place

import org.springframework.stereotype.Service
import org.trail.scapes.domain.Category
import org.trail.scapes.domain.Place
import org.trail.scapes.domain.user.User
import org.trail.scapes.dto.place.CreatePlaceDto
import org.trail.scapes.dto.place.PlaceDto
import org.trail.scapes.dto.place.PlaceListItemDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import org.trail.scapes.mappers.category.CategoryMapper
import org.trail.scapes.mappers.image.ImageMapper
import org.trail.scapes.mappers.user.ReviewMapper
import org.trail.scapes.repositories.PlaceRepository

@Service
class PlaceMapper(
    private val imageMapper: ImageMapper,
    private val reviewMapper: ReviewMapper,
    private val categoryMapper: CategoryMapper,
    private val placeRepository: PlaceRepository
): ExtendedEntityMapper<Place, PlaceDto, CreatePlaceDto> {

    override fun toDto(entity: Place): PlaceDto {
        return PlaceDto(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            address = entity.address,
            latitude = entity.latitude,
            longitude = entity.longitude,
            createdByUserId = entity.createdBy.id,
            categories = entity.categories.map { categoryMapper.toDto(it) },
            images = entity.images.map { imageMapper.toDto(it) },
            reviews = entity.reviews.map { reviewMapper.toDto(it) },
            imagesCount = placeRepository.countImagesByPlaceId(entity.id!!),
            reviewsCount = placeRepository.countReviewsByPlaceId(entity.id!!),
            averageRating = placeRepository.averageRatingByPlaceId(entity.id!!)
                ?.takeIf { !it.isNaN() }
        )
    }

    override fun toEntity(dto: PlaceDto): Place {
        return Place(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            address = dto.address,
            latitude = dto.latitude,
            longitude = dto.longitude,
            createdBy = User().apply { id = dto.createdByUserId }
        ).also { place ->
            place.categories = dto.categories.map {
                Category().apply { id = it.id }
            }.toMutableList()
            place.images = dto.images.map { imageMapper.toEntity(it).apply { this.place = place } }.toMutableList()
            place.reviews = dto.reviews.map { reviewMapper.toEntity(it).apply { this.place = place } }.toMutableList()
        }
    }

    override fun fromCreateDto(dto: CreatePlaceDto): Place {
        return Place(
            id = null,
            title = dto.title,
            description = dto.description,
            address = dto.address,
            latitude = dto.latitude,
            longitude = dto.longitude,
            createdBy = User().apply { id = dto.createdByUserId }
        ).also { place ->
            place.categories = dto.categoryIds.map {
                Category().apply { id = it }
            }.toMutableList()
            place.images = dto.images.map { imageMapper.fromCreateDto(it).apply { this.place = place } }.toMutableList()
        }
    }

    fun toListItem(entity: Place): PlaceListItemDto = PlaceListItemDto(
        id = entity.id,
        title = entity.title,
        latitude = entity.latitude,
        longitude = entity.longitude,
        thumbnailUrl = entity.images.firstOrNull()?.url
    )
}

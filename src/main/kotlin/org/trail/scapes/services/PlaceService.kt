package org.trail.scapes.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.trail.scapes.domain.Place
import org.trail.scapes.dto.image.CreateImageDto
import org.trail.scapes.dto.place.CreatePlaceDto
import org.trail.scapes.dto.place.PlaceDto
import org.trail.scapes.dto.place.PlaceListItemDto
import org.trail.scapes.mappers.place.PlaceMapper
import org.trail.scapes.repositories.CategoryRepository
import org.trail.scapes.repositories.PlaceRepository
import org.trail.scapes.repositories.UserRepository

@Service
open class PlaceService(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val imageService: ImageService,
    private val placeMapper: PlaceMapper
) {

    @Transactional
    open fun create(dto: CreatePlaceDto, currentUserId: Long): PlaceDto {
        if (dto.createdByUserId != currentUserId) {
            throw IllegalArgumentException("createdByUserId must match current user")
        }

        if (placeRepository.existsByTitleAndLatitudeAndLongitude(dto.title, dto.latitude, dto.longitude)) {
            throw IllegalStateException("Place with same title and coordinates already exists")
        }

        val user = userRepository.findById(currentUserId)
            .orElseThrow { NoSuchElementException("User with id=$currentUserId not found") }

        val categories = if (dto.categoryIds.isEmpty()) emptyList()
        else categoryRepository.findAllById(dto.categoryIds)

        val place = Place(
            title = dto.title,
            description = dto.description,
            address = dto.address,
            latitude = dto.latitude,
            longitude = dto.longitude,
            createdBy = user
        ).apply { this.categories.addAll(categories) }

        val saved = placeRepository.save(place)

        if (dto.images.isNotEmpty()) {
            dto.images.forEach { imageService.createForPlace(saved.id!!, it) }
        }

        val reloaded = placeRepository.findById(saved.id!!)
            .orElseThrow { IllegalStateException("Saved place not found") }

        return placeMapper.toDto(reloaded)
    }

    @Transactional(readOnly = true)
    open fun getById(placeId: Long): PlaceDto =
        placeRepository.findById(placeId)
            .map { placeMapper.toDto(it) }
            .orElseThrow { NoSuchElementException("Place with id=$placeId not found") }

    @Transactional(readOnly = true)
    open fun listAll(): List<PlaceDto> =
        placeRepository.findAll()
            .map { placeMapper.toDto(it) }

    @Transactional(readOnly = true)
    open fun listItems(): List<PlaceListItemDto> =
        placeRepository.findAll().map { placeMapper.toListItem(it) }

    @Transactional(readOnly = true)
    open fun searchByTitle(query: String): List<PlaceListItemDto> =
        placeRepository.findByTitleContainingIgnoreCase(query.trim())
            .map { placeMapper.toListItem(it) }

    @Transactional(readOnly = true)
    open fun findByCategory(categoryId: Long): List<PlaceListItemDto> =
        placeRepository.findByCategoriesId(categoryId)
            .map { placeMapper.toListItem(it) }

    @Transactional(readOnly = true)
    open fun findWithinRadius(lat: Double, lon: Double, radiusKm: Double): List<PlaceListItemDto> =
        placeRepository.findWithinRadius(lat, lon, radiusKm)
            .map { placeMapper.toListItem(it) }

    @Transactional
    open fun update(
        placeId: Long,
        currentUserId: Long,
        payload: CreatePlaceDto
    ): PlaceDto {
        val place = placeRepository.findById(placeId)
            .orElseThrow { NoSuchElementException("Place with id=$placeId not found") }

        if (place.createdBy.id != currentUserId) {
            throw IllegalStateException("Only owner can update the place")
        }

        payload.title.let { place.title = it }
        payload.description.let { place.description = it }
        payload.address.let { place.address = it }
        payload.latitude.let { place.latitude = it }
        payload.longitude.let { place.longitude = it }

        payload.categoryIds.let { ids ->
            val categories = categoryRepository.findAllById(ids)
            place.categories.clear()
            place.categories.addAll(categories)
        }

        val saved = placeRepository.save(place)
        return placeMapper.toDto(saved)
    }

    @Transactional
    open fun addImages(placeId: Long, currentUserId: Long, images: List<CreateImageDto>): PlaceDto {
        val place = placeRepository.findById(placeId)
            .orElseThrow { NoSuchElementException("Place with id=$placeId not found") }

        if (place.createdBy.id != currentUserId) {
            throw IllegalStateException("Only owner can add images")
        }

        images.forEach { imageService.createForPlace(placeId, it) }

        val reloaded = placeRepository.findById(placeId)
            .orElseThrow { IllegalStateException("Place not found after images add") }

        return placeMapper.toDto(reloaded)
    }

    @Transactional
    open fun removeImage(placeId: Long, imageId: Long, currentUserId: Long): PlaceDto {
        val place = placeRepository.findById(placeId)
            .orElseThrow { NoSuchElementException("Place with id=$placeId not found") }

        if (place.createdBy.id != currentUserId) {
            throw IllegalStateException("Only owner can remove images")
        }

        imageService.deleteById(imageId)

        val reloaded = placeRepository.findById(placeId)
            .orElseThrow { IllegalStateException("Place not found after image remove") }

        return placeMapper.toDto(reloaded)
    }

    @Transactional
    open fun delete(placeId: Long, currentUserId: Long) {
        val place = placeRepository.findById(placeId)
            .orElseThrow { NoSuchElementException("Place with id=$placeId not found") }

        if (place.createdBy.id != currentUserId) {
            throw IllegalStateException("Only owner can delete the place")
        }

        place.images.forEach { img -> imageService.deleteById(img.id!!) }

        placeRepository.delete(place)
    }
}
package org.trail.scapes.mappers.user

import org.springframework.stereotype.Service
import org.trail.scapes.domain.Image
import org.trail.scapes.domain.Place
import org.trail.scapes.domain.user.Review
import org.trail.scapes.domain.user.User
import org.trail.scapes.dto.user.UserDto
import org.trail.scapes.dto.user.UserListItemDto
import org.trail.scapes.dto.user.auth.UserRegistrationDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import org.trail.scapes.repositories.ImageRepository
import org.trail.scapes.repositories.PlaceRepository
import org.trail.scapes.repositories.ReviewRepository

@Service
class UserMapper(
    private val placeRepository: PlaceRepository,
    private val reviewRepository: ReviewRepository,
    private val imageRepository: ImageRepository
): ExtendedEntityMapper<User, UserDto, UserRegistrationDto> {

    override fun toDto(entity: User): UserDto {
        val userId = entity.id

        val favoriteIds   = userId?.let { placeRepository.findFavoritePlaceIdsByUserId(it) } ?: emptyList()
        val addedPlaceIds = userId?.let { placeRepository.findUsersAddedPlaceIdsByUserId(it) } ?: emptyList()
        val reviewIds     = userId?.let { reviewRepository.findReviewIdsByUserId(it) } ?: emptyList()

        val favoritesCount    = userId?.let { placeRepository.countFavoritePlacesByUserId(it).toInt() }
        val addedPlacesCount  = userId?.let { placeRepository.countUsersAddedPlacesByUserId(it).toInt() }
        val reviewsCount      = userId?.let { reviewRepository.countReviewsByUserId(it).toInt() }

        return UserDto(
            id = userId,
            username = entity.username,
            email = entity.email,
            firstName = entity.firstName,
            lastName = entity.lastName,
            dateOfBirth = entity.dateOfBirth,
            profileImageId = entity.profileImage?.id,
            profileImageUrl = entity.profileImage?.url,
            favoritePlaceIds = favoriteIds,
            usersAddedPlaceIds = addedPlaceIds,
            reviewIds = reviewIds,
            favoritesCount = favoritesCount,
            addedPlacesCount = addedPlacesCount,
            reviewsCount = reviewsCount
        )
    }

    override fun toEntity(dto: UserDto): User {
        val user = User(
            id = dto.id,
            username = dto.username,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            dateOfBirth = dto.dateOfBirth,
            password = "",
            profileImage = dto.profileImageId?.let { Image().apply { id = it } }
        )

        user.favoritePlaces = dto.favoritePlaceIds
            .map { placeId -> Place().apply { id = placeId } }
            .toMutableList()

        user.usersAddedPlaces = dto.usersAddedPlaceIds
            .map { placeId -> Place().apply { id = placeId; createdBy = user } }
            .toMutableList()

        user.reviews = dto.reviewIds
            .map { reviewId -> Review().apply { id = reviewId; author = user } }
            .toMutableList()

        return user
    }

    override fun fromCreateDto(dto: UserRegistrationDto): User {
        return User(
            id = null,
            username = dto.username,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            dateOfBirth = dto.dateOfBirth,
            password = dto.password,
            profileImage = dto.profileImageId?.let { Image().apply { id = it } }
        ).apply {
            favoritePlaces = mutableListOf()
            usersAddedPlaces = mutableListOf()
            reviews = mutableListOf()
        }
    }

    fun toListItem(entity: User): UserListItemDto =
        UserListItemDto(
            id = entity.id,
            username = entity.username,
            firstName = entity.firstName,
            lastName = entity.lastName,
            profileImageUrl = entity.profileImage?.url
        )
}
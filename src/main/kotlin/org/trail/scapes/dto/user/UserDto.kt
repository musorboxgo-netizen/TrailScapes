package org.trail.scapes.dto.user

import java.time.LocalDate
import java.util.Collections.emptyList

data class UserDto(
    val id: Long?,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val profileImageId: Long?,
    val profileImageUrl: String?,
    val favoritePlaceIds: List<Long> = emptyList(),
    val usersAddedPlaceIds: List<Long> = emptyList(),
    val reviewIds: List<Long> = emptyList(),

    val favoritesCount: Int? = null,
    val addedPlacesCount: Int? = null,
    val reviewsCount: Int? = null
)

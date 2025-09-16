package org.trail.scapes.dto.user

data class UserListItemDto(
    val id: Long?,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String? = null
)
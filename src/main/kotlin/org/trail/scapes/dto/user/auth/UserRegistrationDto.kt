package org.trail.scapes.dto.user.auth

import java.time.LocalDate

data class UserRegistrationDto(
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val password: String,
    val profileImageId: Long? = null
)
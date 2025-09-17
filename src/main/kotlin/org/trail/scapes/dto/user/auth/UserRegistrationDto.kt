package org.trail.scapes.dto.user.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import org.trail.scapes.dto.image.CreateImageDto
import java.time.LocalDate

data class UserRegistrationDto(
    @field:NotBlank val username: String,
    @field:Email val email: String,
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    @field:Past val dateOfBirth: LocalDate,
    @field:Size(min = 8, max = 128) val password: String,
    val profileImage: CreateImageDto? = null,
    val profileImageId: Long? = null
)
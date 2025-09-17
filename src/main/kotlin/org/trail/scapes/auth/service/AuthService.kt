package org.trail.scapes.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.trail.scapes.domain.user.User
import org.trail.scapes.dto.user.auth.UserAuthRequest
import org.trail.scapes.dto.user.auth.UserRegistrationDto
import org.trail.scapes.mappers.user.UserMapper
import org.trail.scapes.repositories.ImageRepository
import org.trail.scapes.repositories.UserRepository
import org.trail.scapes.services.ImageService

@Service
open class AuthService(
    private val userMapper: UserMapper,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val imageService: ImageService,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    open fun register(dto: UserRegistrationDto): AuthResponse {
        require(!userRepository.existsByUsername(dto.username)) { "Username already taken" }
        require(!userRepository.existsByEmail(dto.email)) { "Email already registered" }

        val user: User = userMapper.fromCreateDto(dto).apply {
            password = passwordEncoder.encode(dto.password)
        }
        val saved = userRepository.save(user)

        dto.profileImage?.let { createImageDto ->
            imageService.setUserProfileImage(saved.id!!, createImageDto)
        }

        dto.profileImageId?.let { imageId ->
            imageService.attachExistingProfileImage(saved, imageId)
        }

        val token = jwtService.generateToken(saved.id!!, saved.username)
        return AuthResponse(token)
    }

    fun login(req: UserAuthRequest): AuthResponse {
        val user = userRepository.findByUsername(req.username)
            ?: throw NoSuchElementException("User not found")
        if (!passwordEncoder.matches(req.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        return AuthResponse(jwtService.generateToken(user.id!!, user.username))
    }


}
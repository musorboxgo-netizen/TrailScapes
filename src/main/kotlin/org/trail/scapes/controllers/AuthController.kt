package org.trail.scapes.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.trail.scapes.auth.service.AuthService
import org.trail.scapes.dto.user.auth.UserAuthRequest
import org.trail.scapes.dto.user.auth.UserAuthResponse
import org.trail.scapes.dto.user.auth.UserRegistrationDto

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@RequestBody dto: UserRegistrationDto): ResponseEntity<UserAuthResponse> =
        ResponseEntity.ok(authService.register(dto))

    @PostMapping("/login")
    fun login(@RequestBody req: UserAuthRequest): ResponseEntity<UserAuthResponse> =
        ResponseEntity.ok(authService.login(req))
}
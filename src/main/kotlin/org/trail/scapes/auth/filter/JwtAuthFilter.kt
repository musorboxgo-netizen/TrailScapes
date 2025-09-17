package org.trail.scapes.auth.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import org.trail.scapes.auth.service.JwtService
import org.trail.scapes.configuration.properties.JwtProperties
import org.trail.scapes.repositories.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.trail.scapes.domain.user.User

@Service
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val props: JwtProperties,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(props.header) ?: ""
        if (!header.startsWith(props.prefix)) {
            chain.doFilter(req, res); return
        }
        val token = header.removePrefix(props.prefix).trim()
        val userId = jwtService.parseUserId(token)

        if (userId != null && SecurityContextHolder.getContext().authentication == null) {
            val user: User? = userRepository.findById(userId).orElse(null)
            if (user != null) {
                val auth = UsernamePasswordAuthenticationToken(user, null, emptyList())
                auth.details = WebAuthenticationDetailsSource().buildDetails(req)
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        chain.doFilter(req, res)
    }
}
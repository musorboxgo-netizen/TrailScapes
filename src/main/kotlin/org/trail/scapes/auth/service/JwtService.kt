package org.trail.scapes.auth.service

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import org.trail.scapes.configuration.properties.JwtProperties
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.util.*

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {
    private val key: SecretKey = run {
        // поддержка как Base64-секрета, так и «сырой» строки
        val bytes = try {
            Decoders.BASE64.decode(jwtProperties.secret)
        } catch (_: Exception) {
            jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        }
        require(bytes.size >= 32) { "JWT secret must be at least 256 bits (32 bytes)" }
        Keys.hmacShaKeyFor(bytes)
    }

    fun generateToken(userId: Long, username: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.expiration.toMillis())
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("username", username)
            .setIssuer(jwtProperties.issuer)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun parseUserId(token: String): Long? = try {
        val jwt = Jwts.parserBuilder()
            .requireIssuer(jwtProperties.issuer)
            .setAllowedClockSkewSeconds(jwtProperties.clockSkew.seconds)
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
        jwt.body.subject.toLong()
    } catch (_: Exception) {
        null
    }
}
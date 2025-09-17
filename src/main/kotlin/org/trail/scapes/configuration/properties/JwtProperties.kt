package org.trail.scapes.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "security.jwt")
data class JwtProperties(
    val secret: String,
    val issuer: String = "app",
    val expiration: Duration = Duration.ofHours(1),
    val clockSkew: Duration = Duration.ofSeconds(0),
    val header: String = "Authorization",
    val prefix: String = "Bearer "
)
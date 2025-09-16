package org.trail.scapes.configuration

import com.cloudinary.Cloudinary
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CloudinaryConfig {

    @Value("\${cloudinary.cloud_name}")
    lateinit var cloudName: String

    @Value("\${cloudinary.api_key}")
    lateinit var apiKey: String

    @Value("\${cloudinary.api_secret}")
    lateinit var apiSecret: String

    @Bean
    open fun cloudinary(): Cloudinary {
        val config = hashMapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret,
            "secure" to true
        )
        return Cloudinary(config)
    }
}
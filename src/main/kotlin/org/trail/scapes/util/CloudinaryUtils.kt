package org.trail.scapes.util

object CloudinaryUtils {
    fun extractPublicIdFromUrl(url: String): String? {
        val regex = Regex(""".*/upload/v\d+/(.*)\.[a-zA-Z0-9]+$""")
        val m = regex.find(url) ?: return null
        return m.groupValues[1]
    }
}

package org.trail.scapes.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.trail.scapes.domain.Image

@Repository
interface ImageRepository: JpaRepository<Image, Long> {

    @Query("SELECT i.url FROM Image i WHERE i.id = :id")
    fun findUrlById(@Param("id") id: Long): String?
}
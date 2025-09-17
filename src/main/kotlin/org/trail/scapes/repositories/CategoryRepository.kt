package org.trail.scapes.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.trail.scapes.domain.Category
import java.util.*

@Repository
interface CategoryRepository: JpaRepository<Category, Long> {
    @Query("SELECT COUNT(DISTINCT p.id) FROM Place p JOIN p.categories c WHERE c.id = :categoryId")
    fun countPlacesByCategoryId(@Param("categoryId") categoryId: Long): Long

    fun findByNameIgnoreCase(name: String): Optional<Category>

    fun existsByNameIgnoreCase(name: String): Boolean
}
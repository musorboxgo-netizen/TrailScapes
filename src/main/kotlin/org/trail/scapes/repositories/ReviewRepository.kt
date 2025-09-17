package org.trail.scapes.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.trail.scapes.domain.user.Review

@Repository
interface ReviewRepository: JpaRepository<Review, Long> {

    @Query("""
        SELECT r.id
        FROM Review r
        WHERE r.author.id = :userId
    """)
    fun findReviewIdsByUserId(@Param("userId") userId: Long): List<Long>

    @Query("""
        SELECT count(r)
        FROM Review r
        WHERE r.author.id = :userId
    """)
    fun countReviewsByUserId(@Param("userId") userId: Long): Long

    fun findAllByAuthorIdOrderByCreatedAtDesc(authorId: Long): List<Review>

    fun findAllByPlaceIdOrderByCreatedAtDesc(placeId: Long): List<Review>
}
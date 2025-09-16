package org.trail.scapes.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.trail.scapes.domain.user.friendship.Friendship

interface FriendshipRepository : JpaRepository<Friendship, Long> {

    @Query("""
        SELECT f FROM Friendship f
        WHERE f.userLow.id = :lowId AND f.userHigh.id = :highId
          AND f.status = 'ACTIVE'
    """)
    fun findActiveByPair(@Param("lowId") lowId: Long, @Param("highId") highId: Long): Friendship?

    @Query("""
        SELECT f FROM Friendship f
        JOIN FETCH f.userLow ul
        JOIN FETCH f.userHigh uh
        WHERE (ul.id = :userId OR uh.id = :userId)
          AND f.status = 'ACTIVE'
    """)
    fun findActiveByUserId(@Param("userId") userId: Long, pageable: Pageable): List<Friendship>

    @Query("""
        SELECT COUNT(f) FROM Friendship f
        WHERE (f.userLow.id = :userId OR f.userHigh.id = :userId)
          AND f.status = 'ACTIVE'
    """)
    fun countActiveByUserId(@Param("userId") userId: Long): Long
}
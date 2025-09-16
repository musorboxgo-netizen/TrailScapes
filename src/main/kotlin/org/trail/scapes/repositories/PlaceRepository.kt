package org.trail.scapes.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.trail.scapes.domain.Place

@Repository
interface PlaceRepository: JpaRepository<Place, Long> {

    @Query("SELECT COUNT(i) FROM Image i WHERE i.place.id = :placeId")
    fun countImagesByPlaceId(@Param("placeId") placeId: Long): Int

    @Query("SELECT COUNT(r) FROM Review r WHERE r.place.id = :placeId")
    fun countReviewsByPlaceId(@Param("placeId") placeId: Long): Int

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place.id = :placeId")
    fun averageRatingByPlaceId(@Param("placeId") placeId: Long): Double?

    @Query("""
        SELECT p.id 
        FROM User u 
        JOIN u.favoritePlaces p 
        WHERE u.id = :userId
    """)
    fun findFavoritePlaceIdsByUserId(@Param("userId") userId: Long): List<Long>

    @Query("""
        SELECT count(p) 
        FROM User u 
        JOIN u.favoritePlaces p 
        WHERE u.id = :userId
    """)
    fun countFavoritePlacesByUserId(@Param("userId") userId: Long): Long

    @Query("""
        SELECT p.id
        FROM Place p
        WHERE p.createdBy.id = :userId
    """)
    fun findUsersAddedPlaceIdsByUserId(@Param("userId") userId: Long): List<Long>

    @Query("""
        SELECT count(p)
        FROM Place p
        WHERE p.createdBy.id = :userId
    """)
    fun countUsersAddedPlacesByUserId(@Param("userId") userId: Long): Long
}
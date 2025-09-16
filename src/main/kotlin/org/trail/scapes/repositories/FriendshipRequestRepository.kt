package org.trail.scapes.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.trail.scapes.domain.user.friendship.FriendshipRequest

@Repository
interface FriendshipRequestRepository: JpaRepository<FriendshipRequest, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(fr) > 0 THEN TRUE ELSE FALSE END
        FROM FriendshipRequest fr
        WHERE fr.status = 'PENDING'
          AND (
              (fr.requester.id = :u1 AND fr.addressee.id = :u2) OR
              (fr.requester.id = :u2 AND fr.addressee.id = :u1)
          )
    """)
    fun existsPendingBetween(@Param("u1") u1: Long, @Param("u2") u2: Long): Boolean

    @Query("""
        SELECT fr FROM FriendshipRequest fr
        WHERE fr.status = 'PENDING'
          AND fr.requester.id = :requesterId
          AND fr.addressee.id = :addresseeId
    """)
    fun findPendingByRequesterAndAddressee(
        @Param("requesterId") requesterId: Long,
        @Param("addresseeId") addresseeId: Long
    ): FriendshipRequest?

    @Query("""
        SELECT fr FROM FriendshipRequest fr
        WHERE fr.status = 'PENDING' AND fr.addressee.id = :userId
        ORDER BY fr.sentAt DESC
    """)
    fun findAllPendingForAddressee(@Param("userId") userId: Long): List<FriendshipRequest>

    @Query("""
        SELECT fr FROM FriendshipRequest fr
        WHERE fr.status = 'PENDING' AND fr.requester.id = :userId
        ORDER BY fr.sentAt DESC
    """)
    fun findAllPendingForRequester(@Param("userId") userId: Long): List<FriendshipRequest>
}
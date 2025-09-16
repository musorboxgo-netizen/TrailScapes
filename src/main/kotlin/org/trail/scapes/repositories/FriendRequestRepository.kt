package org.trail.scapes.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.trail.scapes.domain.user.friendship.FriendshipRequest

@Repository
interface FriendRequestRepository: JpaRepository<FriendshipRequest, Long> {
}
package org.trail.scapes.mappers.user

import org.hibernate.Hibernate
import org.springframework.stereotype.Service
import org.trail.scapes.domain.enums.FriendshipRequestStatus
import org.trail.scapes.domain.user.User
import org.trail.scapes.domain.user.friendship.FriendshipRequest
import org.trail.scapes.dto.user.friendship.CreateFriendshipRequestDto
import org.trail.scapes.dto.user.friendship.FriendshipRequestDto
import org.trail.scapes.mappers.ExtendedEntityMapper
import java.time.LocalDateTime

@Service
class FriendshipRequestMapper(
    private val userMapper: UserMapper,
): ExtendedEntityMapper<FriendshipRequest, FriendshipRequestDto, CreateFriendshipRequestDto> {

    override fun toDto(entity: FriendshipRequest): FriendshipRequestDto =
        FriendshipRequestDto(
            id = entity.id,
            requesterId = entity.requester.id!!,
            addresseeId = entity.addressee.id!!,
            status = entity.status,
            sentAt = entity.sentAt,
            message = entity.message,
            requester = if (Hibernate.isInitialized(entity.requester)) userMapper.toListItem(entity.requester) else null,
            addressee = if (Hibernate.isInitialized(entity.addressee)) userMapper.toListItem(entity.addressee) else null
        )

    override fun toEntity(dto: FriendshipRequestDto): FriendshipRequest =
        FriendshipRequest(
            id = dto.id,
            requester = User().apply { id = dto.requesterId },
            addressee = User().apply { id = dto.addresseeId },
            status = dto.status,
            sentAt = dto.sentAt,
            message = dto.message
        )

    override fun fromCreateDto(dto: CreateFriendshipRequestDto): FriendshipRequest {
        throw UnsupportedOperationException(
            "Use fromCreateDto(dto, requesterId) to prevent spoofing requester identity"
        )
    }

    fun fromCreateDto(dto: CreateFriendshipRequestDto, requesterId: Long): FriendshipRequest =
        FriendshipRequest(
            id = null,
            requester = User().apply { id = requesterId },
            addressee = User().apply { id = dto.addresseeId },
            status = FriendshipRequestStatus.PENDING,
            sentAt = LocalDateTime.now(),
            message = dto.message
        )

}
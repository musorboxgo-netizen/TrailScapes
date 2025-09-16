package org.trail.scapes.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.trail.scapes.domain.enums.FriendshipStatus
import org.trail.scapes.domain.enums.FriendshipRequestStatus
import org.trail.scapes.domain.user.friendship.Friendship
import org.trail.scapes.domain.user.friendship.FriendshipStatusDto
import org.trail.scapes.dto.user.friendship.CreateFriendshipRequestDto
import org.trail.scapes.dto.user.friendship.FriendshipRequestDto
import org.trail.scapes.mappers.user.FriendshipRequestMapper
import org.trail.scapes.repositories.FriendshipRepository
import org.trail.scapes.repositories.FriendshipRequestRepository
import org.trail.scapes.repositories.UserRepository
import org.trail.scapes.util.normalizePair
import java.time.LocalDateTime

@Service
open class FriendRequestService(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
    private val friendshipRequestRepository: FriendshipRequestRepository,
    private val friendshipRequestMapper: FriendshipRequestMapper
) {
    /**
     * Отправить заявку в друзья.
     * @param requesterId — ID текущего пользователя (инициатора)
     * @param dto — данные для создания заявки (адресат + сообщение)
     */
    fun sendRequest(requesterId: Long, dto: CreateFriendshipRequestDto): FriendshipRequestDto {
        if (requesterId == dto.addresseeId) {
            throw IllegalArgumentException("User can't send friend request to himself.")
        }

        val requester = userRepository.findById(requesterId)
            .orElseThrow { NoSuchElementException("Requester with id=$requesterId not found") }

        val addressee = userRepository.findById(dto.addresseeId)
            .orElseThrow { NoSuchElementException("Addressee with id=${dto.addresseeId} not found") }

        // уже друзья?
        val (lowId, highId) = normalizePair(requester.id!!, addressee.id!!)
        if (friendshipRepository.existsActiveByPair(lowId, highId)) {
            throw IllegalStateException("Users are already friends")
        }

        // заявка уже существует и ожидает решения? (в обе стороны)
        if (friendshipRequestRepository.existsPendingBetween(requester.id!!, addressee.id!!)) {
            throw IllegalStateException("Pending friend request already exists between users")
        }

        // создать и сохранить заявку
        val entity = friendshipRequestMapper.fromCreateDto(dto, requesterId)
        val saved = friendshipRequestRepository.save(entity)

        // (опционально) создать уведомление адресату — вынеси в NotificationService при желании
        // notificationService.notifyFriendRequest(saved)

        return friendshipRequestMapper.toDto(saved)
    }

    /**
     * Принять заявку.
     * Разрешено только адресату этой заявки и только в статусе PENDING.
     * Возвращает DTO обновлённой заявки.
     */
    fun acceptRequest(requestId: Long, currentUserId: Long): FriendshipRequestDto {
        val request = friendshipRequestRepository.findById(requestId)
            .orElseThrow { NoSuchElementException("Friend request with id=$requestId not found") }

        if (request.status != FriendshipRequestStatus.PENDING) {
            throw IllegalStateException("Request is not pending")
        }
        if (request.addressee.id != currentUserId) {
            throw IllegalStateException("Only addressee can accept this request")
        }

        // создать дружбу, если её ещё нет
        val (lowId, highId) = normalizePair(request.requester.id!!, request.addressee.id!!)
        if (!friendshipRepository.existsActiveByPair(lowId, highId)) {
            val friendship = Friendship(
                id = null,
                userLow = userRepository.getReferenceById(lowId),
                userHigh = userRepository.getReferenceById(highId),
                status = FriendshipStatus.ACTIVE,
                since = LocalDateTime.now(),
                createdByUserId = currentUserId
            )
            friendshipRepository.save(friendship)
        }

        // отметить заявку как принятую (оставляем для истории)
        request.status = FriendshipRequestStatus.ACCEPTED
        val saved = friendshipRequestRepository.save(request)

        // (опционально) уведомить инициатора, что его заявку приняли
        // notificationService.notifyInfo(request.requester.id!!, "...")

        return friendshipRequestMapper.toDto(saved)
    }

    /**
     * Отклонить заявку.
     * Разрешено только адресату и только для PENDING.
     */
    fun declineRequest(requestId: Long, currentUserId: Long): FriendshipRequestDto {
        val request = friendshipRequestRepository.findById(requestId)
            .orElseThrow { NoSuchElementException("Friend request with id=$requestId not found") }

        if (request.status != FriendshipRequestStatus.PENDING) {
            throw IllegalStateException("Request is not pending")
        }
        if (request.addressee.id != currentUserId) {
            throw IllegalStateException("Only addressee can decline this request")
        }

        request.status = FriendshipRequestStatus.DECLINED
        val saved = friendshipRequestRepository.save(request)

        // (опционально) уведомить инициатора об отклонении
        // notificationService.notifyInfo(request.requester.id!!, "...")

        return friendshipRequestMapper.toDto(saved)
    }

    /**
     * Отменить собственную исходящую заявку (пока она PENDING).
     * Разрешено только инициатору.
     */
    fun cancelRequest(requestId: Long, currentUserId: Long) {
        val request = friendshipRequestRepository.findById(requestId)
            .orElseThrow { NoSuchElementException("Friend request with id=$requestId not found") }

        if (request.status != FriendshipRequestStatus.PENDING) {
            throw IllegalStateException("Only pending request can be canceled")
        }
        if (request.requester.id != currentUserId) {
            throw IllegalStateException("Only requester can cancel this request")
        }

        // можно либо пометить DECLINED, либо удалить запись — выбирай политику.
        friendshipRequestRepository.delete(request)

        // (опционально) почистить/пометить уведомление у адресата
        // notificationService.dismissFriendRequestNotification(request.id!!)
    }

    /**
     * Входящие PENDING-заявки для пользователя (он — адресат).
     */
    @Transactional(readOnly = true)
    open fun incomingPending(userId: Long): List<FriendshipRequestDto> =
        friendshipRequestRepository.findAllPendingForAddressee(userId)
            .map { friendshipRequestMapper.toDto(it) }

    /**
     * Исходящие PENDING-заявки пользователя (он — инициатор).
     */
    @Transactional(readOnly = true)
    open fun outgoingPending(userId: Long): List<FriendshipRequestDto> =
        friendshipRequestRepository.findAllPendingForRequester(userId)
            .map { friendshipRequestMapper.toDto(it) }

    /**
     * Текущий статус отношений между двумя пользователями.
     * NONE / FRIENDS / OUTGOING_PENDING / INCOMING_PENDING
     */
    @Transactional(readOnly = true)
    open fun relationshipStatus(viewerId: Long, otherUserId: Long): FriendshipStatusDto {
        val (lowId, highId) = normalizePair(viewerId, otherUserId)
        if (friendshipRepository.existsActiveByPair(lowId, highId)) {
            return FriendshipStatusDto(FriendshipStatus.ACTIVE)
        }

        // есть входящая/исходящая PENDING-заявка?
        val outgoing = friendshipRequestRepository.findPendingByRequesterAndAddressee(viewerId, otherUserId)
        if (outgoing != null) return FriendshipStatusDto(FriendshipStatus.OUTGOING_PENDING)

        val incoming = friendshipRequestRepository.findPendingByRequesterAndAddressee(otherUserId, viewerId)
        if (incoming != null) return FriendshipStatusDto(FriendshipStatus.INCOMING_PENDING)

        return FriendshipStatusDto(FriendshipStatus.NONE)
    }
}
package com.plgdhd.userservice.service

import com.plgdhd.auth.event.proto.UserBannedEvent
import com.plgdhd.auth.event.proto.UserRegisteredEvent
import com.plgdhd.auth.event.proto.UserRoleChangedEvent
import com.plgdhd.userservice.common.UserRole
import com.plgdhd.userservice.common.UserStatus
import com.plgdhd.userservice.dto.request.UpdateUserRequest
import com.plgdhd.userservice.dto.response.UserPrivateResponse
import com.plgdhd.userservice.dto.response.UserPublicResponse
import com.plgdhd.userservice.exception.UserAlreadyExistsException
import com.plgdhd.userservice.exception.UserNotFoundException
import com.plgdhd.userservice.mapper.UserMapper
import com.plgdhd.userservice.model.ProcessedEvent
import com.plgdhd.userservice.model.User
import com.plgdhd.userservice.repository.ProcessedEventRepository
import com.plgdhd.userservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val processedEventRepository: ProcessedEventRepository,
    private val userMapper: UserMapper
) {

    private val log = LoggerFactory.getLogger(UserService::class.java)


    /* переделаю, думаю говнокод тут */
    @Transactional
    fun createUser(event: UserRegisteredEvent, topic: String) {

        // TODO вынести в сервис идемпотентности, AOP?
        try {
            processedEventRepository.save(ProcessedEvent(event.eventId, topic))

            processedEventRepository.flush()
        } catch (ex: DataIntegrityViolationException) {

            log.warn("Ивент {} уже обрабатывается или обработано", event.eventId)
            return
        }

        /* TODO для всех таких случаев придумать обработку */
        val uuid = UUID.fromString(event.userId)

        if (userRepository.existsById(uuid)) {
            log.warn("Пользователь {} уже существует", uuid)
            processedEventRepository.save(ProcessedEvent(event.eventId, topic))
            return
        }

        val user = User(
            id = uuid,
            username = event.username,
            email = event.email,
            role = UserRole.fromString(event.role)
        )

        userRepository.save(user)

        log.info(
            "Пользователь создан: userId={}, username={}",
            event.userId, event.username
        )
    }

    @Cacheable("userProfiles", key = "#userId")
    fun getPublicProfile(userId: String): UserPublicResponse {
        val user = findUserById(userId)
        return userMapper.toPublicResponse(user)
    }

    fun getCurrentProfile(jwt: Jwt): UserPrivateResponse {

        val userId = jwt.subject
        val user = findUserById(userId)
        return userMapper.toPrivateResponse(user)
    }

    @Cacheable("userProfiles", key = "#userId")
    fun getPublicProfileByUsername(username: String): UserPublicResponse {

        val user = userRepository.findByUsername(username)
            ?: throw UserNotFoundException("username: $username")

        return userMapper.toPublicResponse(user)
    }

    @Transactional
    @CacheEvict("userProfiles", key = "#jwt.subject")
    fun updateCurrentProfile(
        jwt: Jwt, request: UpdateUserRequest
    ): UserPrivateResponse {

        val userId = jwt.subject
        val user = findUserById(userId)

        request.username?.let { newUsername ->
            if (newUsername != user.username && userRepository.existsByUsername(newUsername)) {
                throw UserAlreadyExistsException(newUsername)
            }
            user.username = newUsername
        }

        user.apply {
            request.bio?.let { bio = it }

            request.avatarUrl?.let { avatarUrl = it }
        }

        val saved = userRepository.save(user)

        /*
        по идее с Keycloak связан только  email и я не даю возможность его изменить
        поэтому в будущем нужно переделать, сейчас мне лень и я глупый
        */

        log.info("Обновлен: userId={}", userId)
        return userMapper.toPrivateResponse(saved)
    }

    @Transactional
    @CacheEvict("userProfiles", key = "#jwt.subject")
    fun bunUser(userBannedEvent: UserBannedEvent, topic: String) {

        if (processedEventRepository.existsById(userBannedEvent.eventId)) return

        val uuid = UUID.fromString(userBannedEvent.userId)

        userRepository.updateStatus(uuid, UserStatus.BANNED)
        processedEventRepository.save(ProcessedEvent(userBannedEvent.eventId, topic))

        log.info("Пользователь заблокирован: userId={}", userBannedEvent.userId)
    }

    @Transactional
    @CacheEvict("userProfiles", key = "#jwt.subject")
    fun updateUserRole(userRoleChangedEvent: UserRoleChangedEvent, topic: String) {

        if (processedEventRepository.existsById(userRoleChangedEvent.eventId)) return

        val uuid = UUID.fromString(userRoleChangedEvent.userId)

        userRepository.updateRole(uuid, UserRole.fromString(userRoleChangedEvent.newRole))
        processedEventRepository.save(ProcessedEvent(userRoleChangedEvent.eventId, topic))

        log.info("Обновлена роль для пользователя {} на {} ", uuid, userRoleChangedEvent.newRole)
    }

    private fun findUserById(userId: String): User {

        val uuid = try {

            UUID.fromString(userId)
        } catch (e: IllegalArgumentException) {
            throw UserNotFoundException(userId)
        }

        return userRepository.findById(uuid)
            .orElseThrow { UserNotFoundException(userId) }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    fun cleanupProcessedEvents() {

        val cutoff = Instant.now().minusSeconds(30L * 24 * 60 * 60)

        val deleted = processedEventRepository.deleteOlderThan(cutoff)

        if (deleted > 0) log.info("Удалены устаревшие ивенты: {}", deleted)
    }

}
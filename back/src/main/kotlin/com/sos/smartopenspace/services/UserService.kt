package com.sos.smartopenspace.services

import com.google.common.hash.Hashing
import com.sos.smartopenspace.domain.*
import com.sos.smartopenspace.persistence.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoderService: PasswordEncoderService
) {
    @Value("\${user.reset.token.lifetime}")
    private val resetTokenLifetime: Long = 0

    fun create(user: User): User {
        try {
            user.password = securePassword(user.password)
            return userRepository.saveAndFlush(user)
        } catch (error: DataIntegrityViolationException) {
            throw BadRequestException("El mail ya esta en uso")
        }
    }

    @Transactional(readOnly = true)
    fun auth(email: String, password: String): User {
        val user = findByEmail(email)
        if (!passwordEncoderService.matchesPassword(password, user.password)) {
            throw UserUnauthorizedException()
        }
        return user
    }

    fun resetPassword(email: String, resetToken: String, password: String): User {
        val hashedToken = hash(resetToken)
        val user = userRepository.findByEmailAndResetToken(email, hashedToken) ?: throw UserUnauthorizedException()
        if (user.resetTokenLifetime == null || user.resetTokenLifetime!! < System.currentTimeMillis()) {
            throw UnauthorizedException("El token esta vencido")
        }

        user.cleanResetToken()
        user.password = passwordEncoderService.encodePassword(password)
        return user
    }

    private fun hash(password: String) = Hashing.sha256()
        .hashString(password, StandardCharsets.UTF_8)
        .toString()

    @Transactional(readOnly = true)
    fun findById(id: Long) = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

    @Transactional(readOnly = true)
    fun findByEmail(email: String) = userRepository.findByEmail(email) ?: throw UserNotFoundException()

    fun generatePasswordResetToken(user: User): String {
        val random = ByteArray(64)
        val token = convertToBase64(random.toString())
            .replace("=", "")
            .replace("/", "")
            .replace("+", "")

        user.secureResetToken(token, resetTokenLifetime)

        return token
    }

    private fun convertToBase64(str: String): String {
        return Base64.getEncoder().encodeToString(str.toByteArray())
    }

    private fun securePassword(password: String): String {
        return passwordEncoderService.encodePassword(password)
    }


}
package com.sos.smartopenspace.service

import com.sos.smartopenspace.services.PasswordEncoderService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class PasswordEncoderServiceTest {

    private val passwordEncoderService = PasswordEncoderService()

    @Test
    fun `encode password`() {
        // Given
        val password = "password123!"

        // When
        val result = passwordEncoderService.encodePassword(password)

        // Then
        assertNotEquals(password, result)
    }

    @Test
    fun `encode and match password`() {
        val password = "password123!"
        val encodedPassword = passwordEncoderService.encodePassword(password)

        assertTrue(passwordEncoderService.matchesPassword(password, encodedPassword))
        assertFalse(passwordEncoderService.matchesPassword("random_value_but_not_password", encodedPassword))
    }
}
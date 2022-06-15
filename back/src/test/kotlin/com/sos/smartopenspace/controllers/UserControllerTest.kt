package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.domain.User
import com.sos.smartopenspace.persistence.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var repoUser: UserRepository

    @Test
    fun `creating a valid user returns an ok status response`() {
        val userName = "User"
        val userEmail = "user@mail.com"

        val body = """
            {
                "email": "$userEmail",
                "name": "$userName"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/user")
                .contentType("application/json")
                .content(body)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userName))

    }
    @Test
    fun `create a user with an used email returns an bad request status response`() {
        val userName = "User"
        val userEmail = "user@mail.com"
        repoUser.save(User(userEmail, userName))

        val body = """
            {
                "email": "$userEmail",
                "name": "$userName"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/user")
                .contentType("application/json")
                .content(body)
        ).andExpect(MockMvcResultMatchers.status().is4xxClientError)

    }

}
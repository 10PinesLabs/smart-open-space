package com.sos.smartopenspace.controllers

import com.jayway.jsonpath.JsonPath
import com.sos.smartopenspace.domain.User
import com.sos.smartopenspace.persistence.OpenSpaceRepository
import com.sos.smartopenspace.persistence.UserRepository
import com.sos.smartopenspace.services.UserService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
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

  @Autowired
  lateinit var userService: UserService

  @Test
  fun `user registration returns ok status response`() {
    val email = "email@gmail.com"
    val password = "password"
    val name = "Fran"
    val userInformation = aUserCreationBody(email = email, password = password, name = name)

    val response = mockMvc.perform(
      MockMvcRequestBuilders.post("/user")
        .contentType("application/json")
        .content(userInformation)
    ).andExpect(MockMvcResultMatchers.status().isOk)
     .andReturn().response

    val id = JsonPath.read<Int>(response.contentAsString, "$.id").toLong()
    assertNotNull(repoUser.findByIdOrNull(id))
  }

  @Test
  fun `user registration with existing mail returns error response`() {
    val email = "email@gmail.com"
    val password = "password"
    val name = "Fran"
    val userInformation = aUserCreationBody(email = email, password = password, name = name)

    mockMvc.perform(
      MockMvcRequestBuilders.post("/user")
        .contentType("application/json")
        .content(userInformation)
    ).andReturn().response

    mockMvc.perform(
      MockMvcRequestBuilders.post("/user")
        .contentType("application/json")
        .content(userInformation)
    ).andExpect(MockMvcResultMatchers.status().isBadRequest)
  }

  @Test
  fun `user login returns ok status response`() {
      val email = "email@gmail.com"
      val password = "password"
      val name = "Fran"
      val userInformation = aUserCreationBody(email = email, password = password, name = name)

      mockMvc.perform(
              MockMvcRequestBuilders.post("/user")
                      .contentType("application/json")
                      .content(userInformation)
      )

    val userLoginInformation = """
          {
                "email": "${email}",
                "password": "${password}"
          }
      """
    mockMvc.perform(
      MockMvcRequestBuilders.post("/user/auth")
        .contentType("application/json")
        .content(userLoginInformation)
    ).andExpect(MockMvcResultMatchers.status().isOk)
  }

    @Test
    fun `user login returns not found status response`() {
        val email = "email@gmail.com"
        val password = "password"
        userService.create(User(email= email, name = "Fran", password = password))

        val userLoginInformation = """
          {
                "email": "${email}",
                "password": "OtraPassword"
          }
      """
        mockMvc.perform(
            MockMvcRequestBuilders.post("/user/auth")
                .contentType("application/json")
                .content(userLoginInformation)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

  @Test
  fun `user reset password with correct token updates password`() {
    val user = userService.create(User(email = "email@gmail.com", name = "Fran", password = "password"))
    val resetToken = userService.generatePasswordResetToken(user)
    val anotherPassword = "OtraPassword"

    val userResetPasswordInformation = """
          {
                "email": "${user.email}",
                "password": "$anotherPassword",
                "resetToken": "$resetToken"
          }
      """
    mockMvc.perform(
      MockMvcRequestBuilders.post("/user/reset")
        .contentType("application/json")
        .content(userResetPasswordInformation)
    ).andExpect(MockMvcResultMatchers.status().isOk)

    val userLoginInformation = """
          {
                "email": "${user.email}",
                "password": "$anotherPassword"
          }
      """
    mockMvc.perform(
      MockMvcRequestBuilders.post("/user/auth")
        .contentType("application/json")
        .content(userLoginInformation)
    ).andExpect(MockMvcResultMatchers.status().isOk)
  }

  @Test
  fun `user reset password with invalid token throws not found`() {
    val user = userService.create(User(email = "email@gmail.com", name = "Fran", password = "password"))
    userService.generatePasswordResetToken(user)
    val anotherPassword = "OtraPassword"

    val userResetPasswordInformation = """
          {
                "email": "${user.email}",
                "password": "$anotherPassword",
                "resetToken": "ivalidtokenn"
          }
      """
    mockMvc.perform(
      MockMvcRequestBuilders.post("/user/reset")
        .contentType("application/json")
        .content(userResetPasswordInformation)
    ).andExpect(MockMvcResultMatchers.status().isNotFound)
  }

  private fun aUserCreationBody(email: String, password: String, name: String): String {
      return """
{
    "email": "${email}",
    "name": "${name}",
    "password": "${password}"
}
        """
  }
}

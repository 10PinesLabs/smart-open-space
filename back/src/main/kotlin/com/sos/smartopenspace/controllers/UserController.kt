package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.domain.User
import com.sos.smartopenspace.dto.request.RecoveryEmailRequestDTO
import com.sos.smartopenspace.dto.request.UserLoginRequestDTO
import com.sos.smartopenspace.dto.request.UserValidateTokenRequestDTO
import com.sos.smartopenspace.services.EmailService
import com.sos.smartopenspace.services.UserService
import com.sos.smartopenspace.translators.response.UserResTranslator
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("user")
class UserServiceREST(private val userService: UserService, private val emailService: EmailService) {
  @PostMapping
  fun create(@Valid @RequestBody user: User) =
    UserResTranslator.translateFrom(userService.create(user))

  @PostMapping("/auth")
  fun auth(@Valid @RequestBody user: UserLoginRequestDTO) =
    UserResTranslator.translateFrom(userService.auth(user.email, user.password))

  @PostMapping("/recovery")
  fun sendRecoveryEmail(@Valid @RequestBody user: RecoveryEmailRequestDTO) =
    UserResTranslator.translateFrom(emailService.sendRecoveryEmail(user.email))

  @PostMapping("/reset")
  fun resetPassword(@Valid @RequestBody user: UserValidateTokenRequestDTO) =
    UserResTranslator.translateFrom(userService.resetPassword(user.email, user.resetToken, user.password))
}

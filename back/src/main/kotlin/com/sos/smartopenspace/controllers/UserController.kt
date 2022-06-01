package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.domain.User
import com.sos.smartopenspace.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

class UserData(
  @field:NotEmpty(message = "Ingrese un email")
  @field:Email
  val email: String,
  @field:NotEmpty(message = "Ingrese una contraseña")
  @field:NotBlank(message = "Contraseña no puede ser vacía")
  val password: String
)

data class Data(val data: User?)

@RestController
@RequestMapping("/undefined/user") // TODO: Revert undefined prefix
class UserServiceREST(private val userService: UserService) {
  @PostMapping
  fun create(@Valid @RequestBody user: User) = userService.create(user)

  @PostMapping("/auth")
  fun auth(@Valid @RequestBody user: UserData) = userService.auth(user.email, user.password)

  @GetMapping("/identify/{email}/")
  fun identify(@Email @PathVariable email: String) = Data(userService.identify(email))
}

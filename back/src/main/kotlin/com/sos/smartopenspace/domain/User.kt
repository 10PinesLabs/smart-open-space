package com.sos.smartopenspace.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

@Entity(name = "Users")
class User(
  @field:NotEmpty(message = "Ingrese un email")
  @field:Email
  @Column(unique = true)
  val email: String,

  @field:NotEmpty(message = "Ingrese un nombre")
  @field:NotBlank(message = "Nombre no puede ser vacío")
  val name: String,

  @field:NotEmpty(message = "Ingrese una contraseña")
  @field:NotBlank(message = "Contraseña no puede ser vacía")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  var password: String = "",

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  var resetToken: String? = null,

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  var resetTokenLifetime: Long? = null,

  @Id @GeneratedValue
  var id: Long = 0
) {

  fun addOpenSpace(openSpace: OpenSpace): User {
    openSpace.organizer = this
    return this
  }

  fun checkOwnershipOf(openSpace: OpenSpace) {
    if (this != openSpace.organizer)
      throw UserNotOwnerOfOpenSpaceException()
  }

  fun checkOwnershipOf(talk: Talk) {
    if (!isOwnerOf(talk))
      throw UserNotOwnerOfTalkException()
  }

  fun isOwnerOf(talk: Talk) = this == talk.speaker

  fun secureResetToken(resetToken: String, lifetime: Long) {
    this.resetToken = secureField(resetToken)
    this.resetTokenLifetime = System.currentTimeMillis() + lifetime
  }

  fun cleanResetToken() {
    this.resetToken = null
    this.resetTokenLifetime = null
  }

  private fun secureField(field: String) = Hashing.sha256()
    .hashString(field, StandardCharsets.UTF_8)
    .toString()
}

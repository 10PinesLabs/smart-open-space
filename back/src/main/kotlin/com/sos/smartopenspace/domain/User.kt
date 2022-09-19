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

  @Id @GeneratedValue
  var id: Long = 0
) {

  fun addTalk(talk: Talk): User {
    talk.speaker = this
    return this
  }

  fun addOpenSpace(openSpace: OpenSpace): User {
    openSpace.organizer = this
    return this
  }

  fun checkOwnershipOf(talk: Talk) {
      if (!isOwnerOf(talk))
        throw UserNotOwnerOfTalkException()
  }

  fun isOwnerOf(talk: Talk) = this == talk.speaker

  fun securePassword() {
    password = Hashing.sha256()
      .hashString(password, StandardCharsets.UTF_8)
      .toString()
  }

    fun removeTalk(talk: Talk) {
      talks.remove(talk)
    }
}

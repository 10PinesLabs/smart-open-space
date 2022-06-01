package com.sos.smartopenspace.helpers

import java.net.URL
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

class CreateTalkDTO(
    @field:NotEmpty(message = "Ingrese un nombre")
    @field:NotBlank(message = "Nombre no puede ser vacío")
    val name: String,

    val description: String = "",

    val meetingLink: URL? = null,

    val trackId: Long? = null
)

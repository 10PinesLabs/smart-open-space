package com.sos.smartopenspace.dto.request

import com.sos.smartopenspace.domain.Document
import java.net.URL
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class CreateTalkRequestDTO(
    @field:NotEmpty(message = "Ingrese un nombre")
    @field:NotBlank(message = "Nombre no puede ser vacío")
    val name: String,

    val description: String = "",

    val meetingLink: URL? = null,

    val trackId: Long? = null,

    @field:Valid
    val documents: Set<Document> = emptySet()
)

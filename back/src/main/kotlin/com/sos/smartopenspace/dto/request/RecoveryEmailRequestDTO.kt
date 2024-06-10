package com.sos.smartopenspace.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class RecoveryEmailRequestDTO(
    @field:NotEmpty(message = "Ingrese un email")
    @field:Email
    val email: String
)
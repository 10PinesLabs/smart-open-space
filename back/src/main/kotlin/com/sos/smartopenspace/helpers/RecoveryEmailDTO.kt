package com.sos.smartopenspace.helpers

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class RecoveryEmailDTO(
    @field:NotEmpty(message = "Ingrese un email")
    @field:Email
    val email: String
)
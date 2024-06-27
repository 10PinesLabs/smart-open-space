package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.User
import com.sos.smartopenspace.dto.response.UserResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object UserResTranslator : TranslatorFrom<User, UserResponseDTO> {
    override fun translateFrom(domain: User) = UserResponseDTO(
        id = domain.id,
        email = domain.email,
        name = domain.name,
    )
}
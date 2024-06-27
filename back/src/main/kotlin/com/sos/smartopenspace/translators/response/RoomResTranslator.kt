package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.Room
import com.sos.smartopenspace.dto.response.RoomResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object RoomResTranslator : TranslatorFrom<Room, RoomResponseDTO> {
    override fun translateFrom(domain: Room) = RoomResponseDTO(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        link = domain.link,
    )
}
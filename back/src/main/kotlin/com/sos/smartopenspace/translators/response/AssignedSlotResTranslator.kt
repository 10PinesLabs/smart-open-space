package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.AssignedSlot
import com.sos.smartopenspace.dto.response.AssignedSlotResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object AssignedSlotResTranslator : TranslatorFrom<AssignedSlot, AssignedSlotResponseDTO> {
    override fun translateFrom(domain: AssignedSlot) =
        AssignedSlotResponseDTO(
            id = domain.id,
            slot = TalkSlotResTranslator.translateFrom(domain.slot),
            room = RoomResTranslator.translateFrom(domain.room),
            talk = TalkResTranslator.translateFrom(domain.talk),
        )

}
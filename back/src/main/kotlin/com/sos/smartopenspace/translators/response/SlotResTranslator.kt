package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.OtherSlot
import com.sos.smartopenspace.domain.Slot
import com.sos.smartopenspace.domain.TalkSlot
import com.sos.smartopenspace.dto.response.OtherSlotResponseDTO
import com.sos.smartopenspace.dto.response.SlotResponseDTO
import com.sos.smartopenspace.dto.response.TalkSlotResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object SlotResTranslator : TranslatorFrom<Slot, SlotResponseDTO> {
    override fun translateFrom(domain: Slot): SlotResponseDTO = when (domain) {
        is TalkSlot -> TalkSlotResTranslator.translateFrom(domain)
        is OtherSlot -> OtherSlotResTranslator.translateFrom(domain)
        else -> throw IllegalArgumentException("unknown slot type")
    }
}

object TalkSlotResTranslator : TranslatorFrom<TalkSlot, TalkSlotResponseDTO> {
    override fun translateFrom(domain: TalkSlot) = TalkSlotResponseDTO(
        id = domain.id,
        startTime = domain.startTime,
        endTime = domain.endTime,
        date = domain.date,
    )
}

object OtherSlotResTranslator : TranslatorFrom<OtherSlot, OtherSlotResponseDTO> {
    override fun translateFrom(domain: OtherSlot) = OtherSlotResponseDTO(
        id = domain.id,
        startTime = domain.startTime,
        endTime = domain.endTime,
        date = domain.date,
        description = domain.description,
    )
}
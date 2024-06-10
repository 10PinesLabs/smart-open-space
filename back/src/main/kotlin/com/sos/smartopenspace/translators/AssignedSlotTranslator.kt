package com.sos.smartopenspace.translators

import com.sos.smartopenspace.domain.AssignedSlot
import com.sos.smartopenspace.domain.TalkSlot
import com.sos.smartopenspace.dto.SimpleIdDTO
import com.sos.smartopenspace.dto.response.*

class AssignedSlotResTranslator: TranslatorFrom<AssignedSlot, AssignedSlotResponseDTO> {
    override fun translateFrom(domain: AssignedSlot): AssignedSlotResponseDTO {
        return AssignedSlotResponseDTO(
            id = domain.id,
            slot = TalkSlotResponseDTO(
                id = domain.slot.id,
                type = TalkSlot::class.simpleName!!,
                startTime = domain.slot.startTime,
                endTime = domain.slot.endTime,
                date = domain.slot.date,
            ),
            room = SimpleIdDTO(domain.room.id),
            talk = TalkResponseDTO(
                id = domain.talk.id,
                name = domain.talk.name,
                description = domain.talk.description,
                meetingLink = domain.talk.meetingLink?.toString(),
                track = domain.talk.track?.let {
                    TrackResponseDTO(
                        id = it.id,
                        name = it.name,
                    )
                },
                speaker = SpeakerResponseDTO(id = domain.talk.speaker.id, name = domain.talk.speaker.name),
                documents = domain.talk.documents.map { SimpleIdDTO(it.id) },
            ),
        )
    }
}
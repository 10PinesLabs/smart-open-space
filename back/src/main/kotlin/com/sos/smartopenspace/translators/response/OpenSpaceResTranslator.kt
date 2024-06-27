package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.OpenSpace
import com.sos.smartopenspace.dto.response.OpenSpaceResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object OpenSpaceResTranslator : TranslatorFrom<OpenSpace, OpenSpaceResponseDTO> {
    override fun translateFrom(domain: OpenSpace) = OpenSpaceResponseDTO(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        urlImage = domain.urlImage,
        queueState = domain.queueState,
        isActiveCallForPapers = domain.isActiveCallForPapers,
        startTime = domain.startTime(),
        endTime = domain.endTime(),
        startDate = domain.startDate(),
        endDate = domain.endDate(),
        pendingQueue = domain.isPendingQueue(),
        activeQueue = domain.isActiveQueue(),
        finishedQueue = domain.isFinishedQueue(),
        amountOfTalks = domain.amountOfTalks(),
        dates = domain.dates(),
        organizer = UserResTranslator.translateFrom(domain.organizer),
        rooms = RoomResTranslator.translateAllFrom(domain.rooms.toList()),
        slots = SlotResTranslator.translateAllFrom(domain.slots.toList()),
        tracks = TrackResTranslator.translateAllFrom(domain.tracks.toList()),
        toSchedule = TalkResTranslator.translateAllFrom(domain.toSchedule.toList()),
        freeSlots = domain.freeSlots().map { (room, slots) ->
            RoomResTranslator.translateFrom(room) to SlotResTranslator.translateAllFrom(slots.toList())
        },
        assignableSlots = domain.assignableSlots().map { (room, slots) ->
            RoomResTranslator.translateFrom(room) to SlotResTranslator.translateAllFrom(slots.toList())
        },
        isActiveVoting = domain.isActiveVoting,
        showSpeakerName = domain.showSpeakerName,
    )
}
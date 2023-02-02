package com.sos.smartopenspace

import com.sos.smartopenspace.domain.*
import com.sos.smartopenspace.persistence.TalkRepository
import java.time.LocalDate
import java.time.LocalTime

fun anOpenSpace(
        slots: Set<Slot> = setOf(
        TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now()),
        TalkSlot(LocalTime.parse("09:30"), LocalTime.parse("10:45"), LocalDate.now()) ,
        TalkSlot(LocalTime.parse("10:45"), LocalTime.parse("11:00"), LocalDate.now())
    ),
        name: String = "os",
        rooms: Set<Room> = setOf(Room("1")),
        tracks: Set<Track> = emptySet(),
        description: String = "",
        talks: MutableSet<Talk> = mutableSetOf(),
        organizer: User = aUser()
): OpenSpace {
    return OpenSpace(
      name = name,
      rooms = rooms,
      slots = slots,
      talks = talks,
      description = description,
      tracks = tracks,
        organizer = organizer
    )
}

fun aSavedTalk(talkRepository: TalkRepository, openSpace: OpenSpace, user: User): Talk {
    openSpace.toggleCallForPapers(user)
    val aTalk = Talk("a name", description = "first description", speaker = user)
    openSpace.addTalk(aTalk)
    talkRepository.save(aTalk)
    return aTalk
}

fun generateTalkBody(name: String = "asdf", description: String = "a generic description", aMeeting: String = "http://aGenericLink.com"): String {
    return """
            {
                "name": "${name}",
                "description": "${description}",
                "meetingLink": "${aMeeting}"
            }
        """.trimIndent()
}

fun anOpenSpaceWith(
        talk: Talk,
        organizer: User,
        slots: Set<Slot> = setOf(
                TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now()),
                TalkSlot(LocalTime.parse("09:30"), LocalTime.parse("10:45"), LocalDate.now()),
                TalkSlot(LocalTime.parse("10:45"), LocalTime.parse("11:00"), LocalDate.now())),
        rooms: Set<Room> = setOf(Room("Sala"))
): OpenSpace {
    val openSpace = anOpenSpace(talks = mutableSetOf(talk), slots = slots, rooms = rooms, organizer = organizer)
    return openSpace
}

fun aUser(userEmail: String = "apprentices@sos.sos"): User {
    return User(userEmail, "apprentices", "apprentices")
}


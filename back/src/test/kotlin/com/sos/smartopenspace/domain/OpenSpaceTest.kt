package com.sos.smartopenspace.domain

import com.sos.smartopenspace.anOpenSpace
import com.sos.smartopenspace.anOpenSpaceWith
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime

class OpenSpaceTest {

    private fun anyOpenSpace(talks: MutableSet<Talk> = mutableSetOf()) =
        OpenSpace(
            "os", emptySet(), setOf(
                TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("10:00")),
                TalkSlot(LocalTime.parse("10:00"), LocalTime.parse("11:00")),
                TalkSlot(LocalTime.parse("11:00"), LocalTime.parse("12:00"))
            ),
            talks
        )

    private fun anyOpenSpaceWith(organizer: User): OpenSpace {
        val openSpace = anyOpenSpace()
        organizer.addOpenSpace(openSpace)
        return openSpace
    }

    private fun anyUser(openSpaces: MutableSet<OpenSpace> = mutableSetOf(), talks: MutableSet<Talk> = mutableSetOf()): User {
        val user = User("augusto@sos.sos", "augusto", "Augusto", talks)
        openSpaces.forEach { user.addOpenSpace(it) }
        return user
    }


    @Test
    fun `an open space is created with necessary fields and contains them`() {
        val nameOpenSpace = "os"
        val date = LocalDate.now()
        val openSpace = OpenSpace(
            nameOpenSpace, emptySet(), emptySet()
        )

        assertEquals(openSpace.name, nameOpenSpace)
    }

    @Test
    fun `an open space is created with description and contains it`() {
        val nameOpenSpace = "os"
        val date = LocalDate.now()
        val description = "A description"
        val openSpace = OpenSpace(
            nameOpenSpace, emptySet(), emptySet(),
            mutableSetOf(), description
        )

        assertEquals(openSpace.description, description)
    }

    @Test
    fun `an open space starts with inactive call for papers`() {
        val organizer = anyUser()
        val openSpace = anyOpenSpaceWith(organizer)

        assertFalse(openSpace.isActiveCallForPapers)
    }

    @Test
    fun `an open space starts a call for papers`() {
        val organizer = anyUser()
        val openSpace = anyOpenSpaceWith(organizer)

        openSpace.toggleCallForPapers(organizer)

        assertTrue(openSpace.isActiveCallForPapers)
    }

    @Test
    fun `a user thats not the organizer cant start call for papers`() {
        val aUser = anyUser()
        val organizer = anyUser()
        val openSpace = anyOpenSpaceWith(organizer)

        assertThrows<NotTheOrganizerException> {
            openSpace.toggleCallForPapers(aUser)
        }
    }

    @Test
    fun `an open space cannot add a talk when call for papers is closed`() {
        val openSpace = anyOpenSpace()

        assertThrows(CallForPapersClosedException::class.java) {
            openSpace.addTalk(Talk("Talk"))
        }
    }

    @Test
    fun `an open space can add a talk when call for papers is open`() {
        val organizer = anyUser()
        val openSpace = anyOpenSpaceWith(organizer)
        openSpace.toggleCallForPapers(organizer)
        val talk = Talk("Talk")

        openSpace.addTalk(talk)

        assertTrue(openSpace.containsTalk(talk))
    }

    @Test
    fun `an open space with no tracks cant add a talk with track`() {
        val organizer = anyUser()
        val openSpace = anyOpenSpaceWith(organizer)
        openSpace.toggleCallForPapers(organizer)
        val aTrack = Track(name = "track", color = "#FFFFFF")
        val aTalk = Talk("Talk", track = aTrack)

        assertThrows<NotValidTrackForOpenSpaceException> {
            openSpace.addTalk(aTalk)
        }
    }

    @Test
    fun `an open space with tracks cant add a talk with a different track`() {
        val aTrack = Track(name = "track", color = "#FFFFFF")
        val anotherTrack = Track(name = "another track", color = "#000000")
        val organizer = anyUser()
        val openSpace = anOpenSpace(tracks = setOf(aTrack))
        organizer.addOpenSpace(openSpace)
        openSpace.toggleCallForPapers(organizer)
        val aTalk = Talk("Talk", track = anotherTrack)

        assertThrows<NotValidTrackForOpenSpaceException> {
            openSpace.addTalk(aTalk)
        }
    }

    @Test
    fun `an open space with tracks cant add a talk without track`() {
        val aTrack = Track(name = "track", color = "#FFFFFF")
        val organizer = anyUser()
        val openSpace = anOpenSpace(tracks = setOf(aTrack))
        organizer.addOpenSpace(openSpace)
        openSpace.toggleCallForPapers(organizer)
        val aTalk = Talk("Talk")

        assertThrows<NotValidTrackForOpenSpaceException> {
            openSpace.addTalk(aTalk)
        }
    }

    @Test
    fun `an open space with tracks can add a talk with track`() {
        val aTrack = Track(name = "track", color = "#FFFFFF")
        val organizer = anyUser()
        val openSpace = anOpenSpace(tracks = setOf(aTrack))
        organizer.addOpenSpace(openSpace)
        openSpace.toggleCallForPapers(organizer)
        val aTalk = Talk("Talk", track = aTrack)

        openSpace.addTalk(aTalk)

        assertTrue(openSpace.containsTalk(aTalk))
    }

    @Test
    fun `an open space finishes a call for papers`() {
        val organizer = anyUser()
        val openSpace = anyOpenSpaceWith(organizer)

        openSpace.toggleCallForPapers(organizer)
        openSpace.toggleCallForPapers(organizer)

        assertFalse(openSpace.isActiveCallForPapers)
    }

    @Test
    fun `an open space is created with a track`() {
        val track = Track(name = "track", color = "#FFFFFF")
        val openSpace = OpenSpace(
            name = "os", rooms = emptySet(), slots = emptySet(),
            talks = mutableSetOf(), tracks = setOf(track)
        )

        assertEquals(1, openSpace.tracks.size)
        assertEquals(track.color, openSpace.tracks.first().color)
        assertEquals(track.name, openSpace.tracks.first().name)
        assertEquals(track.description, openSpace.tracks.first().description)
    }
    @Test
    fun `an openSpace knows when it starts`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val openSpace = openSpaceWithTwoDates(startDate, endDate)

        assertEquals(startDate, openSpace.startDate())
    }

    @Test
    fun `an openSpace knows when it finishes`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val openSpace = openSpaceWithTwoDates(startDate, endDate)

        assertEquals(endDate, openSpace.endDate())
    }

    @Test
    fun `an openSpace knows when is hold`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val openSpace = openSpaceWithTwoDates(startDate, endDate)

        assertEquals(setOf(startDate, endDate), openSpace.dates())
    }

    @Test
    fun `an openSpace removes a talk when scheduled`() {
        val organizer = anyUser()
        val aTalk = Talk("Talk")
        val aSlot = TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now())
        val aRoom = Room("Sala")
        val openSpace = anOpenSpaceWith(organizer = organizer, talk = aTalk, slots = setOf(aSlot), rooms = setOf(aRoom))
        createAndScheduleTalk(openSpace, organizer, aTalk, aSlot, aRoom)

        openSpace.removeTalk(aTalk)

        assertFalse(openSpace.hasAssignedSlots())
    }

    @Test
    fun `an openSpace removes a talk when queued`() {
        val organizer = anyUser()
        val aTalk = Talk("Talk")
        val aSlot = TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now())
        val aRoom = Room("Sala")
        val openSpace = anOpenSpaceWith(organizer = organizer, talk = aTalk, slots = setOf(aSlot), rooms = setOf(aRoom))
        createAndEnqueueTalk(openSpace, organizer, aTalk)

        openSpace.removeTalk(aTalk)

        assertFalse(openSpace.hasQueuedTalks())
    }

    @Test
    fun `an openSpace removes a talk that is to be scheduled`() {
        val organizer = anyUser()
        val aTalk = Talk("Talk")
        val aSlot = TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now())
        val aRoom = Room("Sala")
        val openSpace = anOpenSpaceWith(organizer = organizer, talk = aTalk, slots = setOf(aSlot), rooms = setOf(aRoom))
        createATalkThatIsToBeScheduled(openSpace, organizer, aTalk)

        openSpace.removeTalk(aTalk)

        assertFalse(openSpace.hasTalksToScheduled())
    }

    private fun createAndEnqueueTalk(openSpace: OpenSpace, organizer: User, aTalk: Talk) {
        openSpace.toggleCallForPapers(organizer)
        openSpace.addTalk(aTalk)
        organizer.addTalk(aTalk)
        openSpace.activeQueue(organizer)
        openSpace.enqueueTalk(aTalk)
    }

    private fun createATalkThatIsToBeScheduled(openSpace: OpenSpace, organizer: User, aTalk: Talk) {
        openSpace.toggleCallForPapers(organizer)
        openSpace.addTalk(aTalk)
        organizer.addTalk(aTalk)
        openSpace.activeQueue(organizer)
        openSpace.enqueueTalk(aTalk)
        openSpace.nextTalk(organizer)
    }

    private fun createAndScheduleTalk(openSpace: OpenSpace, organizer: User, aTalk: Talk, aSlot: TalkSlot, aRoom: Room) {
        openSpace.toggleCallForPapers(organizer)
        openSpace.addTalk(aTalk)
        organizer.addTalk(aTalk)
        openSpace.scheduleTalk(aTalk, organizer, aSlot, aRoom)
    }

    private fun openSpaceWithTwoDates(
        startDate: LocalDate?,
        endDate: LocalDate?
    ): OpenSpace {
        val first_date_slot = TalkSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), startDate)
        val end_date_slot = TalkSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), endDate)
        val openSpace = OpenSpace(
            name = "os", rooms = emptySet(), slots = setOf(first_date_slot, end_date_slot),
            talks = mutableSetOf()
        )
        return openSpace
    }
}
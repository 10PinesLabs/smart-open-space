package com.sos.smartopenspace.domain

import com.sos.smartopenspace.anOpenSpace
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class SlotTest {
  private val user = User("anemail@gmail.com", "Pepe")
  private val room1 = Room("1")
  private val talk1 = Talk("talk1", speaker = user)
  private val talk2 = Talk("talk2", speaker = user)
  private val aSlot = TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now())
  private val otherSlot = TalkSlot(LocalTime.parse("09:30"), LocalTime.parse("09:45"), LocalDate.now())
  private val secondDaySlot = TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now().plusDays(1))

  private fun anyUser(talk: Talk): User {
    val user = User("augusto@sos.sos", "Augusto", "Augusto")
    user.addTalk(talk)
    return user
  }

  private fun anyOpenSpaceWithActiveQueued(talks: Set<Talk>, slots: Set<Slot> = setOf(aSlot, otherSlot, secondDaySlot)): OpenSpace {
    val openSpace = anOpenSpace(talks = talks.toMutableSet(), rooms = setOf(room1), slots = slots)
    val organizer = User("augusto@sos.sos", "augusto", "Augusto")
    organizer.addOpenSpace(openSpace)
    openSpace.activeQueue(organizer)
    talks.forEach {
      anyUser(it)
      openSpace.enqueueTalk(it)
      openSpace.nextTalk(organizer)
    }
    return openSpace
  }

  private fun anyOpenSpaceWithOrganizer(
    talks: MutableSet<Talk> = mutableSetOf(talk1, talk2)
  ): OpenSpace {
    val openSpace = anOpenSpace(talks = talks.toMutableSet(), rooms = setOf(room1), slots = setOf(aSlot, otherSlot))
    val organizer = User("augusto@sos.sos", "augusto", "Augusto")
    organizer.addOpenSpace(openSpace)
    openSpace.activeQueue(organizer)
    return openSpace
  }

  @Test
  fun `Si una charla no esta para agendar, no se puede agendar`() {
    val openSpace = anyOpenSpaceWithOrganizer()
    assertThrows(TalkIsNotForScheduledException::class.java) {
      openSpace.scheduleTalk(talk1, anyUser(talk1), aSlot, room1)
    }
  }

  @Test
  fun `El organizador puede agendar una charla siempre`() {
    val openSpace = anyOpenSpaceWithOrganizer()

    val slot = openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)

    assertTrue(slot.startAt(LocalTime.parse("09:00")))
    assertEquals(room1, slot.room)
    assertEquals(talk1, slot.talk)
  }

  @Test
  fun `Asignar una charla en un horario y en una sala`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1))
    val slot = openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    assertTrue(slot.startAt(LocalTime.parse("09:00")))
    assertEquals(room1, slot.room)
    assertEquals(talk1, slot.talk)
  }

  @Test
  fun `No se puede agendar una charla fuera de horario`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1), setOf(aSlot))
    assertThrows(SlotNotFoundException::class.java) {
      openSpace.scheduleTalk(talk1, openSpace.organizer, otherSlot, room1)
    }
  }

  @Test
  fun `Asignar una charla pero el slot esta ocupado`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1, talk2))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    assertThrows(BusySlotException::class.java) {
      openSpace.scheduleTalk(talk2, openSpace.organizer, aSlot, room1)
    }
  }

  @Test
  fun `Asignar una charla que no pertenece al open space`() {
    val openSpace = anyOpenSpaceWithOrganizer()
    assertThrows(TalkDoesntBelongException::class.java) {
      val talk = Talk("otra", speaker = user)
      openSpace.scheduleTalk(talk, anyUser(talk), aSlot, room1)
    }
  }

  @Test
  fun `Asignar una charla que ya se encuentra asignada`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    assertThrows(TalkAlreadyAssignedException::class.java) {
      openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    }
  }

  @Test
  fun `Open space sin charlas agendadas, tiene los slots libres`() {
    val openSpace = anOpenSpace(talks = mutableSetOf(talk1, talk2), rooms = setOf(room1))
    val freeSlots = openSpace.freeSlots()
    assertIterableEquals(
      listOf(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalTime.parse("10:45")),
      slotStartTimes(freeSlots)
    )
  }

  @Test
  fun `Asignar una charlas, ese slot no esta mas libre`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1), setOf(aSlot, otherSlot))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    val freeSlots = openSpace.freeSlots()
    assertFalse(slotStartTimes(freeSlots).contains(LocalTime.parse("09:00")))
  }

  @Test
  fun `Todos los slots asignados no quedan lugares libres`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1), setOf(aSlot))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    val freeSlots = openSpace.freeSlots()
    assertTrue(freeSlots.isEmpty())
  }

  @Test
  fun `Cambiar charla de slot a uno vacio`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    openSpace.exchangeSlot(talk1, room1, otherSlot)
    val freeSlots = openSpace.freeSlots()
    assertTrue(slotStartTimes(freeSlots).contains(LocalTime.parse("09:00")))
    assertFalse(slotStartTimes(freeSlots).contains(LocalTime.parse("09:30")))
  }

  @Test
  fun `Cambiar charla de slot a uno vacio en otra fecha`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    openSpace.exchangeSlot(talk1, room1, secondDaySlot)
    val freeSlots = openSpace.freeSlots()
    assertTrue(slotDate(freeSlots).contains(aSlot.date))
    assertFalse(slotDate(freeSlots).contains(secondDaySlot.date))
  }

  @Test
  fun `Cambiar charla de slot a uno ocupado`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1, talk2), setOf(aSlot, otherSlot))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    openSpace.scheduleTalk(talk2, openSpace.organizer, otherSlot, room1)
    openSpace.exchangeSlot(talk1, room1, otherSlot)
    assertEquals(talk1, openSpace.assignedSlots.find { it.room == room1 && it.startAt(LocalTime.parse("09:30")) }?.talk)
    assertEquals(talk2, openSpace.assignedSlots.find { it.room == room1 && it.startAt(LocalTime.parse("09:00")) }?.talk)
  }

  @Test
  fun `Cambiar charla de slot a uno ocupado en otra fecha`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1, talk2), setOf(aSlot, otherSlot, secondDaySlot))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    openSpace.scheduleTalk(talk2, openSpace.organizer, secondDaySlot, room1)
    openSpace.exchangeSlot(talk1, room1, secondDaySlot)
    assertEquals(talk1, openSpace.assignedSlots.find { it.room == room1 && it.hasDate(secondDaySlot.date) }?.talk)
    assertEquals(talk2, openSpace.assignedSlots.find { it.room == room1 && it.hasDate(aSlot.date) }?.talk)
  }

  @Test
  fun `Cambiar charla de slot pero la charla no esta agendada`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1))
    assertThrows(TalkIsNotScheduledException::class.java) {
      openSpace.exchangeSlot(talk1, room1, aSlot)
    }
  }

  @Test
  fun `Cambiar charla de slot a un horario inexistente`() {
    val openSpace = anyOpenSpaceWithActiveQueued(setOf(talk1))
    openSpace.scheduleTalk(talk1, openSpace.organizer, aSlot, room1)
    assertThrows(SlotNotFoundException::class.java) {
      openSpace.exchangeSlot(talk1, room1, TalkSlot(LocalTime.parse("10:00"), LocalTime.parse("10:30"), LocalDate.now()))
    }
  }

  private fun slotStartTimes(freeSlots: List<Pair<Room, List<Slot>>>) =
    freeSlots[0].second.map { it.startTime }

  private fun slotDate(freeSlots: List<Pair<Room, List<Slot>>>) =
          freeSlots[0].second.map { it.date }

}

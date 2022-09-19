package com.sos.smartopenspace.controllers

import com.jayway.jsonpath.JsonPath
import com.sos.smartopenspace.*
import com.sos.smartopenspace.domain.*
import com.sos.smartopenspace.persistence.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TalkControllerTest {

  @Autowired
  lateinit var slotRepository: SlotRepository

  @Autowired
  lateinit var mockMvc: MockMvc


  @Autowired
  lateinit var userRepository: UserRepository

  @Autowired
  lateinit var openSpaceRepository: OpenSpaceRepository

  @Autowired
  lateinit var talkRepository: TalkRepository

  @Autowired
  lateinit var roomRepository: RoomRepository

  @Test
  fun `schedule a talk returns an ok status response`() {
    val organizer = anySavedUser()
    val talk = anySavedTalk(organizer)
    val room = anySavedRoom()
    val aSlot = aSavedSlot()
    val openSpace = openSpaceRepository.save(anOpenSpaceWith(talk, organizer, setOf(aSlot), setOf(room)))

    mockMvc.perform(
      MockMvcRequestBuilders.put("/talk/schedule/${organizer.id}/${talk.id}/${aSlot.id}/${room.id}")
    ).andExpect(MockMvcResultMatchers.status().isOk)

    assertTrue(openSpace.freeSlots().isEmpty())
  }

  @Test
  fun `when a talk cannot be scheduled it should return a bad request response`() {
    val organizer = anySavedUser()
    val talk = anySavedTalk(organizer)
    val speaker = aSavedUserWithTalk(talk)
    val openSpace = openSpaceRepository.save(anOpenSpaceWith(talk, organizer))
    val slot = openSpace.slots.first()
    val room = anySavedRoom()

    mockMvc.perform(
      MockMvcRequestBuilders.put("/talk/schedule/${speaker.id}/${talk.id}/${slot.id}/${room.id}")
    ).andExpect(MockMvcResultMatchers.status().isBadRequest)
  }

  @Test
  fun `exchange a talk returns an ok status response`() {
    val organizer = anySavedUser()
    val talk = anySavedTalk(organizer)
    val room = anySavedRoom()
    val aSlot = aSavedSlot()
    val otherSlot = otherSavedSlot()
    val openSpace = openSpaceRepository.save(anOpenSpaceWith(talk, organizer, setOf(aSlot, otherSlot), setOf(room)))
    openSpace.scheduleTalk(talk, organizer, aSlot as TalkSlot, room)

    mockMvc.perform(
            MockMvcRequestBuilders.put("/talk/exchange/${talk.id}/${otherSlot.id}/${room.id}")
    ).andExpect(MockMvcResultMatchers.status().isOk)

    assertEquals(1, freeSlots(openSpace).size)
    assertTrue( freeSlots(openSpace).contains(aSlot))
  }

  @Test
  fun `Asking for an specific talk returns an ok status`() {
    val organizer = anySavedUser()
    val talk = anySavedTalk(organizer)
    val openSpace = anySavedOpenSpace()
    organizer.addOpenSpace(openSpace)
    aSavedUserWithTalk(talk)

    mockMvc.perform(
      MockMvcRequestBuilders.get("/talk/${talk.id}")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(talk.id))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(talk.name))
  }

  @Test
  fun `Asking for a talk that not exist returns a not found`() {
    mockMvc.perform(
      MockMvcRequestBuilders.get("/talk/77777")
    ).andExpect(MockMvcResultMatchers.status().isNotFound)
  }


  @Test
  fun `can update a talk correctly`() {
    val user = userRepository.save(aUser())
    val anOpenSpace = anOpenSpace()
    user.addOpenSpace(anOpenSpace)
    anOpenSpace.toggleCallForPapers(user)
    openSpaceRepository.save(anOpenSpace)

    val aTalk = Talk("a talk", speaker = user)
    anOpenSpace.addTalk(aTalk)
    talkRepository.save(aTalk)

    val changedDescription = "a different description"
    val entityResponse = mockMvc.perform(
      MockMvcRequestBuilders.put("/talk/${aTalk.id}/user/${user.id}")
        .contentType("application/json")
        .content(generateTalkBody(description = changedDescription))
    ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response

    val talkId = JsonPath.read<Int>(entityResponse.contentAsString, "$.id")

    mockMvc.perform(
      MockMvcRequestBuilders.get("/openSpace/talks/${anOpenSpace.id}")
    )
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(talkId))
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(changedDescription))
  }

  @Test
  fun `updating an inexistent talk returns a bad request status`() {
    val user = userRepository.save(aUser())
    val anOpenSpace = anOpenSpace()
    user.addOpenSpace(anOpenSpace)
    anOpenSpace.toggleCallForPapers(user)
    openSpaceRepository.save(anOpenSpace)
    val inexistentTalkId = 789

    mockMvc.perform(
      MockMvcRequestBuilders.put("/talk/${inexistentTalkId}/user/${user.id}")
        .contentType("application/json")
        .content(generateTalkBody())
    ).andExpect(MockMvcResultMatchers.status().is4xxClientError)
  }

  @Test
  fun `a talk voted by user returns an ok status response and increase talk votes`() {
    val aUser = anySavedUser()
    val talk = anySavedTalk(aUser)
    aUser.addTalk(talk)

    mockMvc.perform(
      MockMvcRequestBuilders.put("/talk/${talk.id}/user/${aUser.id}/vote")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.votes").value(1))
  }

  @Test
  fun `a talk unvoted by a user returns an ok status response and decrease talk votes`() {
    val aUser = anySavedUser()
    val talk = anySavedTalk(aUser)
    aUser.addTalk(talk)
    talk.addVoteBy(aUser)
    talkRepository.save(talk)

    mockMvc.perform(
      MockMvcRequestBuilders.put("/talk/${talk.id}/user/${aUser.id}/unvote")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.votes").value(0))
  }

  @Test
  fun `a talk cannot be unvoted by a user that didnt vote it returns a bad request`() {
    val aUser = anySavedUser()
    val talk = anySavedTalk(aUser)
    aUser.addTalk(talk)
    talkRepository.save(talk)

    mockMvc.perform(
      MockMvcRequestBuilders.put("/talk/${talk.id}/user/${aUser.id}/unvote")
    ).andExpect(MockMvcResultMatchers.status().isBadRequest)
  }

  private fun anySavedRoom() = roomRepository.save(Room("Sala"))

  private fun anySavedTalk(organizer: User) = talkRepository.save(Talk("Charla", speaker = organizer))

  private fun anySavedUser() = userRepository.save(aUser())

  private fun anySavedOpenSpace() = openSpaceRepository.save(anOpenSpace())

  private fun aSavedUserWithTalk(talk: Talk) =
    userRepository.save(aUser(mutableSetOf(), mutableSetOf(talk), "Pepe@sos.sos"))

  private fun aSavedSlot(): Slot {
    return slotRepository.save(TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"), LocalDate.now()))
  }

  private fun otherSavedSlot(): Slot {
    return slotRepository.save(TalkSlot(LocalTime.parse("09:30"), LocalTime.parse("10:00"), LocalDate.now().plusDays(1)))
  }

  private fun freeSlots(openSpace: OpenSpace) = openSpace.freeSlots().first().second

}
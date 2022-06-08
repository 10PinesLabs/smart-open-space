package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.anOpenSpaceWith
import com.sos.smartopenspace.aUser
import com.sos.smartopenspace.domain.*
import com.sos.smartopenspace.persistence.OpenSpaceRepository
import com.sos.smartopenspace.persistence.RoomRepository
import com.sos.smartopenspace.persistence.TalkRepository
import com.sos.smartopenspace.persistence.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TalkControllerTest {

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
        val talk = anySavedTalk()
        val room = anySavedRoom()
        openSpaceRepository.save(anOpenSpaceWith(talk, organizer, room))
        val time = LocalTime.parse("09:00")

        mockMvc.perform(
                MockMvcRequestBuilders.put("/talk/schedule/${organizer.id}/${talk.id}/${room.id}/${time}")
        ).andExpect(MockMvcResultMatchers.status().isOk)

    }

    @Test
    fun `when a talk cannot be scheduled it should return a bad request response`() {
        val organizer = anySavedUser()
        val talk = anySavedTalk()
        val speaker = aSavedUserWithTalk(talk)
        val room = anySavedRoom()
        val time = LocalTime.parse("09:00")
        openSpaceRepository.save(anOpenSpaceWith(talk, organizer, room))


        mockMvc.perform(
                MockMvcRequestBuilders.put("/talk/schedule/${speaker.id}/${talk.id}/${room.id}/${time}")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `a talk voted by user return an ok status response`() {
        val aUser = anySavedUser()
        val talk = anySavedTalk()
        aUser.addTalk(talk)

        mockMvc.perform(
                MockMvcRequestBuilders.put("/talk/${talk.id}/user/${aUser.id}/vote")
        ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.votes").value(1))
    }

    @Test
    fun `a talk unvoted by a user returns an ok status response`() {
        val aUser = anySavedUser()
        val talk = anySavedTalk()
        aUser.addTalk(talk)
        talk.addVoteBy(aUser)
        talkRepository.save(talk)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/talk/${talk.id}/user/${aUser.id}/unvote")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.votes").value(0))
    }

    private fun anySavedRoom() = roomRepository.save(Room("Sala"))

    private fun anySavedTalk() = talkRepository.save(Talk("Charla"))

    private fun anySavedUser() = userRepository.save(aUser())

    private fun aSavedUserWithTalk(talk: Talk) =
            userRepository.save(aUser(mutableSetOf(), mutableSetOf(talk)))

}
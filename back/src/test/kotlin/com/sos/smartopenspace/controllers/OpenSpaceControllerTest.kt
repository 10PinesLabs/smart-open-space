package com.sos.smartopenspace.controllers

import com.jayway.jsonpath.JsonPath
import com.sos.smartopenspace.anOpenSpace
import com.sos.smartopenspace.domain.*
import com.sos.smartopenspace.persistence.OpenSpaceRepository
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
import java.time.LocalDate
import java.time.LocalTime


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OpenSpaceControllerTest {
    private fun anyUser(oss: MutableSet<OpenSpace> = mutableSetOf(), talks: MutableSet<Talk> = mutableSetOf()) =
        User("augusto@sos.sos", "augusto", "Augusto", oss, talks)

    @Autowired
    lateinit var mockMvc: MockMvc


    @Autowired
    lateinit var repoUser: UserRepository

    @Autowired
    lateinit var repoOpenSpace: OpenSpaceRepository

    @Test
    fun `creating a valid OpenSpace returns an ok status response`() {
        val user = repoUser.save(anyUser())
        val description = "W".repeat(1000)
        val track_color = "#FFFFFF"
        val track_name = "a track"
        val track_description = "W".repeat(500)
        val openSpaceBody = anOpenSpaceCreationBody(
            description = description,
            track = Track(name = track_name, description = track_description, color = track_color)
        )

        val entityResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/openSpace/${user.id}")
                .contentType("application/json")
                .content(openSpaceBody)
        ).andReturn().response
        val id = JsonPath.read<Int>(entityResponse.contentAsString, "$.id")

        mockMvc.perform(
            MockMvcRequestBuilders.get("/openSpace/${id}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActiveCallForPapers").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tracks[0].color").value(track_color))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tracks[0].name").value(track_name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tracks[0].description").value(track_description))
    }

    @Test
    fun `creating an invalid OpenSpace returns a bad request response`() {
        val user = repoUser.save(anyUser())
        val openSpaceBody = anOpenSpaceCreationBody("W".repeat(1001))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/openSpace/${user.id}")
                .contentType("application/json")
                .content(openSpaceBody)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `can create a valid talk and get it correctly`() {
        val user = repoUser.save(anyUser())
        val track = Track("a track", color = "#FFFFFF")
        val anOpenSpace = repoOpenSpace.save(anyOpenSpaceWith(user, setOf(track)))
        anOpenSpace.toggleCallForPapers(user)
        val aMeetingLink = "https://aLink"

        val entityResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/openSpace/talk/${user.id}/${anOpenSpace.id}")
                .contentType("application/json")
                .content(generateTalkWithTrackBody(aMeetingLink, track))
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response

        val talkId = JsonPath.read<Int>(entityResponse.contentAsString, "$.id")
        mockMvc.perform(
            MockMvcRequestBuilders.get("/openSpace/talks/${anOpenSpace.id}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(talkId))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].meetingLink").value(aMeetingLink))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].track.name").value(track.name))
    }

    @Test
    fun `creating an invalid talk return an bad request status`() {
        val user = repoUser.save(anyUser())
        val anOpenSpace = repoOpenSpace.save(anyOpenSpace())
        val anInvalidLink = "invalid link"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/openSpace/talk/${user.id}/${anOpenSpace.id}")
                .contentType("application/json")
                .content(generateTalkBody(anInvalidLink))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `creating a talk when call for papers is closed return an unprocessable entity status`() {
        val user = repoUser.save(anyUser())
        val anOpenSpace = repoOpenSpace.save(anyOpenSpace())
        val aMeetingLink = "https://aLink"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/openSpace/talk/${user.id}/${anOpenSpace.id}")
                .contentType("application/json")
                .content(generateTalkBody(aMeetingLink))
        )
            .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity)
    }

    @Test
    fun `start a call for papers returns an ok status response and the modified Open Space`() {
        val user = repoUser.save(anyUser())
        val anOpenSpace = repoOpenSpace.save(anyOpenSpaceWith(user))

        mockMvc.perform(
            MockMvcRequestBuilders.put("/openSpace/${anOpenSpace.id}/user/${user.id}/callForPapers")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(anOpenSpace.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActiveCallForPapers").value(true))
    }

    @Test
    fun `starting a call for papers with a non organizer user return a bad request status`() {
        val organizer = repoUser.save(anyUser())
        val aUser = repoUser.save(anyUser())
        val anOpenSpace = repoOpenSpace.save(anyOpenSpaceWith(organizer))

        mockMvc.perform(
            MockMvcRequestBuilders.put("/openSpace/${anOpenSpace.id}/user/${aUser.id}/callForPapers")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    private fun anyOpenSpaceWith(organizer: User, tracks: Set<Track>? = null): OpenSpace {
        val openSpace: OpenSpace = if (tracks == null) {
            anOpenSpace()
        } else {
            anOpenSpace(tracks = tracks)
        }
        organizer.addOpenSpace(openSpace)
        return openSpace
    }

    private fun anyOpenSpace(): OpenSpace {
        return OpenSpace(
            "os", LocalDate.now(), setOf(Room("1")),
            setOf(
                TalkSlot(LocalTime.parse("09:00"), LocalTime.parse("09:30"))
            )
        )
    }

    private fun anOpenSpaceCreationBody(
        description: String,
        track: Track = Track(name = "a track", color = "#FFFFFFF")
    ): String {
        return """
{
    "date": "2022-05-11T03:00:00.000Z",
    "name": "asd",
    "description": "${description}",
    "rooms": [{"name": "a"}],
    "slots": [
        {
            "type": "TalkSlot",
            "endTime": [
                0,
                15
            ],
            "startTime": [
                0,
                0
            ]
        }
    ],
    "tracks": [
        {
            "name": "${track.name}",
            "color": "${track.color}",
            "description": "${track.description}"
        }
    ]
}
        """.trimIndent()
    }

    private fun generateTalkBody(aMeeting: String): String {
        return """
            {
                "name": "asdf",
                "meetingLink": "$aMeeting"
            }
        """.trimIndent()
    }

    private fun generateTalkWithTrackBody(aMeeting: String, track: Track): String {
        return """
            {
                "name": "a talk",
                "meetingLink": "$aMeeting",
                "track": {
                    "name": "${track.name}",
                    "description": "${track.description}",
                    "color": "${track.color}"
                }
            }
        """.trimIndent()
    }
}
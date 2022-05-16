package com.sos.smartopenspace.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URL

class TalkTest {
    @Test
    fun `creating a talk with an URL`() {
        val aMeeting = URL("https://meet.google.com/ise-vfbf-yos")
        val aTalk = Talk(name = "aName", description = "aDescription", meeting = aMeeting)
        assertEquals(aMeeting, aTalk.meeting)
    }
}

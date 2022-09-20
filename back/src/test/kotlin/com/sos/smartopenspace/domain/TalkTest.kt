package com.sos.smartopenspace.domain

import com.sos.smartopenspace.aUser
import com.sos.smartopenspace.anOpenSpace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TalkTest {
  @Test
  fun `a talk cant be modified with a track from another OpenSpace`() {
    val aUser = aUser()
    val anOpenSpace = anOpenSpace(organizer = aUser)
    anOpenSpace.toggleCallForPapers(aUser)
    val aTalk = Talk("Una charla", speaker = aUser)
    anOpenSpace.addTalk(aTalk)
    val aTrack = Track("a track name", color = "#FFFFFF")


    assertThrows< NotValidTrackForOpenSpaceException > {
      aTalk.update(name = aTalk.name, description = aTalk.description, track = aTrack, openSpace = anOpenSpace)
    }
  }
}
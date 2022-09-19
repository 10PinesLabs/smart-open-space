package com.sos.smartopenspace.domain

import com.sos.smartopenspace.aUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VoteTest {

    @Test
    fun `a talk has not votes when is created`() {
        val user = anyUser()
        val aTalk = Talk("charla sin votos", speaker = user)

        assertEquals(aTalk.votes(), 0)
    }

    private fun anyUser(): User {
        return User("anemail@gmail.com", "Aname")
    }

    @Test
    fun `a user votes a talk`() {
        val speaker = anyUser()
        val aTalk = Talk("charla sin votos", speaker = speaker)
        val aUser = aUser()

        aTalk.addVoteBy(aUser)

        assertEquals(aTalk.votes(), 1)
    }

    @Test
    fun `a user cannot voted a talk twice`() {
        val speaker = anyUser()
        val aTalk = Talk("charla sin votos", speaker = speaker)
        val aUser = aUser()

        aTalk.addVoteBy(aUser)
        aTalk.addVoteBy(aUser)

        assertEquals(aTalk.votes(), 1)
    }

    @Test
    fun `a user unvotes a talk that he votes before`() {
        val speaker = anyUser()
        val aTalk = Talk("charla sin votos", speaker = speaker)
        val aUser = aUser()
        aTalk.addVoteBy(aUser)

        aTalk.removeVoteBy(aUser)

        assertEquals(aTalk.votes(), 0)
    }

    @Test
    fun `a user cannot unvote a talk that he doesnt vote before`() {
        val speaker = anyUser()
        val aTalk = Talk("charla sin votos", speaker = speaker)
        val aUser = aUser()

        assertThrows<UserDidntVoteThisTalkException> {
            aTalk.removeVoteBy(aUser)
        }
    }
}
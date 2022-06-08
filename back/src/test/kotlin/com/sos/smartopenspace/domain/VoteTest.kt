package com.sos.smartopenspace.domain

import com.sos.smartopenspace.aUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VoteTest {

    @Test
    fun `a talk has not votes when is created`() {
        val aTalk = Talk("charla sin votos")

        assertEquals(aTalk.votes(), 0)
    }

    @Test
    fun `a user votes a talk`() {
        val aTalk = Talk("charla sin votos")
        val aUser = aUser()

        aTalk.addVoteBy(aUser)

        assertEquals(aTalk.votes(), 1)
    }

    @Test
    fun `a user cannot voted a talk twice`() {
        val aTalk = Talk("charla sin votos")
        val aUser = aUser()

        aTalk.addVoteBy(aUser)
        aTalk.addVoteBy(aUser)

        assertEquals(aTalk.votes(), 1)
    }

    @Test
    fun `a user unvotes a talk that he votes before`() {
        val aTalk = Talk("charla sin votos")
        val aUser = aUser()
        aTalk.beingVoted(aUser)

        aTalk.beingUnvoted(aUser)

        assertEquals(aTalk.votes(), 0)
    }

    @Test
    fun `a user cannot unvote a talk that he doesnt vote before`() {
        val aTalk = Talk("charla sin votos")
        val aUser = aUser()

        aTalk.beingUnvoted(aUser)

        assertEquals(aTalk.votes(), 0)
    }
}
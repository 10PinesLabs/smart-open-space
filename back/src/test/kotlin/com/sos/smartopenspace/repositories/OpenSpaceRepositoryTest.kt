package com.sos.smartopenspace.repositories

import com.sos.smartopenspace.aUser
import com.sos.smartopenspace.anOpenSpace
import com.sos.smartopenspace.domain.Track
import com.sos.smartopenspace.persistence.OpenSpaceRepository
import com.sos.smartopenspace.persistence.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class OpenSpaceRepositoryTest {

    @Autowired
    lateinit var repoOpenSpace: OpenSpaceRepository

    @Autowired
    lateinit var repoUser: UserRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `a track cannot be saved with description over 500 characters`() {
        val invalidTrack = Track(name = "name", description = "W".repeat(501), color = "#FFFFFF")
        val user = aUser()
        repoUser.save(user)
        val openSpace = anOpenSpace(tracks = setOf(invalidTrack), organizer = user)

        assertThrows<ConstraintViolationException> {
            repoOpenSpace.save(openSpace)
            entityManager.flush()
        }
    }

    @Test
    fun `a track cannot be saved with blank name`() {
        val invalidTrack = Track(name = "", description = "W".repeat(500), color = "#FFFFFF")
        val user = aUser()
        repoUser.save(user)
        val openSpace = anOpenSpace(tracks = setOf(invalidTrack), organizer = user)

        assertThrows<ConstraintViolationException> {
            repoOpenSpace.save(openSpace)
            entityManager.flush()
        }
    }

    @Test
    fun `a track cannot be saved with incomplete color`() {
        val invalidTrack = Track(name = "aName", description = "W".repeat(500), color = "#FF")
        val user = aUser()
        repoUser.save(user)
        val openSpace = anOpenSpace(tracks = setOf(invalidTrack), organizer = user)

        assertThrows<ConstraintViolationException> {
            repoOpenSpace.save(openSpace)
            entityManager.flush()
        }
    }

    @Test
    fun `a track cannot be saved with a color over 7 characters`() {
        val invalidTrack = Track(name = "aName", description = "W".repeat(500), color = "#FFFFFFF")
        val user = aUser()
        repoUser.save(user)
        val openSpace = anOpenSpace(tracks = setOf(invalidTrack), organizer = user)

        assertThrows<ConstraintViolationException> {
            repoOpenSpace.save(openSpace)
            entityManager.flush()
        }
    }

    @Test
    fun `a track cannot be saved with an invalid color`() {
        val invalidTrack = Track(name = "aName", description = "W".repeat(500), color = "Rojizo")
        val user = aUser()
        repoUser.save(user)
        val openSpace = anOpenSpace(tracks = setOf(invalidTrack), organizer = user)

        assertThrows<ConstraintViolationException> {
            repoOpenSpace.save(openSpace)
            entityManager.flush()
        }
    }

}

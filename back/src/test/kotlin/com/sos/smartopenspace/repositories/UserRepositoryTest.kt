package com.sos.smartopenspace.repositories

import com.sos.smartopenspace.domain.User
import com.sos.smartopenspace.persistence.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceException

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `a track cannot be saved with description over 500 characters`() {
        val email = "user@mail.com"
        val aUser = User(email, "user")
        val anotherUser = User(email, "another user")
        userRepository.save(aUser)

        assertThrows<PersistenceException> {
            userRepository.save(anotherUser)
            entityManager.flush()
        }
    }

}
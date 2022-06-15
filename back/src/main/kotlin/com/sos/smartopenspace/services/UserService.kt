package com.sos.smartopenspace.services

import com.sos.smartopenspace.domain.EmailAlreadyInUseException
import com.sos.smartopenspace.domain.User
import com.sos.smartopenspace.domain.UserNotFoundException
import com.sos.smartopenspace.persistence.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(private val userRepository: UserRepository) {
  fun create(user: User): User {
     if (userRepository.findByEmail(user.email) != null)
       throw EmailAlreadyInUseException()
    return userRepository.save(user)
  }

  @Transactional(readOnly = true)
  fun auth(email: String, password: String) =
    userRepository.findByEmailAndPassword(email, password) ?: throw UserNotFoundException()

  @Transactional(readOnly = true)
  fun findById(id: Long) = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

  @Transactional(readOnly = true)
  fun identify(email: String) = userRepository.findByEmail(email)
}
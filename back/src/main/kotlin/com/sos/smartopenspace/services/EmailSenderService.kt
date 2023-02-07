package com.sos.smartopenspace.services

import com.sos.smartopenspace.domain.Email
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailSenderService(
    private val emailSender: JavaMailSender
) {

    fun sendMail(email: Email) {
        val msg = createSimpleMessage(email)
        emailSender.send(msg)
    }

    private fun createSimpleMessage(email: Email): SimpleMailMessage {
        val message = SimpleMailMessage()
        message.setFrom(System.getenv("spring.mail.username"))
        message.setTo(email.to)
        message.setSubject(email.subject)
        message.setText(email.text)

        return message
    }
}
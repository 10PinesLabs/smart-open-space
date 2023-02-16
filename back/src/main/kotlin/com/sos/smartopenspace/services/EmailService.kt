package com.sos.smartopenspace.services
import com.sos.smartopenspace.domain.Email
import com.sos.smartopenspace.domain.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import javax.mail.Message
import javax.mail.internet.InternetAddress

@Service
class EmailService(
  private val userService: UserService,
  private val emailSender: JavaMailSender
) {

  @Value("\${mail.sender}")
  private val mailSender: String = ""

  @Value("\${frontend.url}")
  private val frontendResetUrl: String = ""

  fun sendRecoveryEmail(email: String): User {
    val user = userService.findByEmail(email)
    val resetToken = userService.generatePasswordResetToken(user)
    sendEmail(email, "recuperación de password", "<html><a href=\"$frontendResetUrl/login?reset=true&email=$email&token=$resetToken\">Resetear contraseña</a></html>")
    return user
  }

  fun sendEmail(email: String, subject: String, text: String) {
    val mail = Email(email, subject, text, withAttachment = false)
    val msg = createMessage(mail)
    emailSender.send(msg)
  }

  private fun createMessage(email: Email) = emailSender.createMimeMessage().apply {
    setFrom(mailSender)
    setRecipient(Message.RecipientType.TO, InternetAddress(email.to))
    subject = email.subject
    setText(email.text, "UTF-8", "html")
    setHeader("Content-Type", "text/html; charset=UTF-8")
  }
}
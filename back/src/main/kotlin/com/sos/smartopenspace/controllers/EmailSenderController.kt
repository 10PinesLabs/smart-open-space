package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.domain.Email
import com.sos.smartopenspace.services.EmailSenderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailSenderController(private val emailSenderService: EmailSenderService) {
    @PostMapping("/mail/send")
    fun sendEmail(@RequestBody mail: Email): ResponseEntity<Void> {
        emailSenderService.sendMail(mail)
        return ResponseEntity.noContent().build()
    }
}
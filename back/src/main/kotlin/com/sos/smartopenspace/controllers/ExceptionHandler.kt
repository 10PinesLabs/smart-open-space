package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.domain.BadRequestException
import com.sos.smartopenspace.domain.UnauthorizedException
import com.sos.smartopenspace.domain.NotFoundException
import com.sos.smartopenspace.domain.UnprocessableEntityException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(BadRequestException::class)
    fun badRequestHandler(exception: Exception) : ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnprocessableEntityException::class)
    fun unprocessableEntityHandler(exception: Exception) : ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(NotFoundException::class)
    fun notFoundHandler(exception: Exception) : ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun notAuthHandler(exception: Exception) : ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.UNAUTHORIZED)
    }
}

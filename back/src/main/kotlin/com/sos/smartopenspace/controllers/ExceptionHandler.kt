package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.domain.BadRequestException
import com.sos.smartopenspace.domain.UnauthorizedException
import com.sos.smartopenspace.domain.NotFoundException
import com.sos.smartopenspace.domain.UnprocessableEntityException
import com.sos.smartopenspace.dto.DefaultErrorDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    //TODO: Add logs

    @ExceptionHandler(BadRequestException::class)
    fun badRequestHandler(exception: Exception): ResponseEntity<DefaultErrorDto> {
        val httpStatus = HttpStatus.BAD_REQUEST
        return ResponseEntity(DefaultErrorDto(exception.message, httpStatus.name), httpStatus)
    }

    @ExceptionHandler(UnprocessableEntityException::class)
    fun unprocessableEntityHandler(exception: Exception): ResponseEntity<DefaultErrorDto> {
        val httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
        return ResponseEntity(DefaultErrorDto(exception.message, httpStatus.name), httpStatus)
    }

    @ExceptionHandler(NotFoundException::class)
    fun notFoundHandler(exception: Exception): ResponseEntity<DefaultErrorDto> {
        val httpStatus = HttpStatus.NOT_FOUND
        return ResponseEntity(DefaultErrorDto(exception.message, httpStatus.name), httpStatus)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun notAuthHandler(exception: Exception): ResponseEntity<DefaultErrorDto> {
        val httpStatus = HttpStatus.UNAUTHORIZED
        return ResponseEntity(DefaultErrorDto(exception.message, httpStatus.name), httpStatus)
    }
}

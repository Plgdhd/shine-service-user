package com.plgdhd.userservice.controller.exception

import com.plgdhd.userservice.dto.exception.ApiErrorResponse
import com.plgdhd.userservice.exception.UserAlreadyExistsException
import com.plgdhd.userservice.exception.UserBannedException
import com.plgdhd.userservice.exception.UserNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AccessDeniedException::class)
    fun AccessDeniedException(ex: AccessDeniedException): ResponseEntity<ApiErrorResponse> {

        log.warn("Обработано исключение AccessDeniedException: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.printStackTrace().toString(),
                ex.message ?: "Нет сообщения",
                ex.file.toString())
            )
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun UserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<ApiErrorResponse> {

        log.warn("Обработано исключение UserAlreadyExistsException: {}", ex.message);

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.printStackTrace().toString(),
                ex.message ?: "Нет сообщения"
            ))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun UserNotFoundException(ex: UserNotFoundException): ResponseEntity<ApiErrorResponse> {

        log.warn("Обработано UserNotFoundException: {}", ex.message)

        return ResponseEntity.badRequest().body(ApiErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.printStackTrace().toString(),
            ex.message ?: "Нет сообщения"
        ))
    }

    @ExceptionHandler(UserBannedException::class)
    fun UserBannedException(ex: UserBannedException): ResponseEntity<ApiErrorResponse> {

        log.warn("Обрабо UserBannedException: {}", ex.message)

        return ResponseEntity.badRequest().body(ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.printStackTrace().toString(),
            ex.message ?: "Нет сообщения"
        ))
    }





}
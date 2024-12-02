package org.pofo.api.common.exception.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pofo.api.common.response.ApiResponse
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = exception.errorCode
        logger.debug { "${errorCode.code} -> ${exception.message}" }
        return ResponseEntity
            .status(errorCode.status)
            .body(ApiResponse.fail(errorCode))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error(exception) { "${exception.message}" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR))
    }
}

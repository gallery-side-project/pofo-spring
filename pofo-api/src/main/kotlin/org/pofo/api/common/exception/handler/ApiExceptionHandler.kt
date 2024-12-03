package org.pofo.api.common.exception.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pofo.api.common.response.ApiResponse
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ApiExceptionHandler {

    /**
     * 인증 오류를 검출합니다.
     */
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(exception: AuthenticationException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.failure(ErrorCode.UNAUTHORIZED))
    }

    /**
     * 인가 오류를 검출합니다.
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(exception: AccessDeniedException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.failure(ErrorCode.FORBIDDEN))
    }

    /**
     * 서비스 로직의 오류를 검출합니다.
     */
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException): ResponseEntity<*> {
        val errorCode = exception.errorCode
        logger.debug { "${errorCode.code} -> ${exception.message}" }
        return ResponseEntity
            .status(errorCode.status)
            .body(ApiResponse.failure(errorCode))
    }

    /**
     * 처리되지 않은 오류를 검출합니다.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<*> {
        logger.error(exception) { "${exception.message}" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR))
    }
}

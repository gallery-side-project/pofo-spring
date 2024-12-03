package org.pofo.api.common.exception.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletResponse
import org.pofo.api.common.response.ApiResponse
import org.pofo.common.exception.CustomException
import org.pofo.common.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

/**
 * 서블릿 내의 에러를 감지합니다.
 * 필터 단에서 검출하는 인증 및 인가 에러는 security 부분을 참고해주세요
 * @see org.pofo.api.security.exception.handler
 */
@RestControllerAdvice
class ApiExceptionHandler {
    private val objectMapper = jacksonObjectMapper()

    /**
     * 인증 오류를 검출합니다.
     */
    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(exception: AuthenticationException): ApiResponse<Nothing> {
        return ApiResponse.failure(ErrorCode.UNAUTHORIZED)
    }

    /**
     * 인가 오류를 검출합니다.
     */
    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(exception: AccessDeniedException): ApiResponse<Nothing> {
        return ApiResponse.failure(ErrorCode.FORBIDDEN)
    }

    /**
     * 서비스 로직의 오류를 검출합니다.
     */
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException, response: HttpServletResponse) {
        val errorCode = exception.errorCode
        logger.debug { "의도된 에러: ${errorCode.code} -> ${exception.message}" }
        response.status = errorCode.status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(response.writer, ApiResponse.failure(errorCode))
    }

    /**
     * 처리되지 않은 오류를 검출합니다.
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exception: Exception): ApiResponse<Nothing> {
        logger.error(exception) { "의도되지 않은 에러: ${exception.message}" }
        return ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}

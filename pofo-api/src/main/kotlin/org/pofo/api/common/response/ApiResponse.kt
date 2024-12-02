package org.pofo.api.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.pofo.common.exception.ErrorCode

data class ApiResponse<T> private constructor(
    private val success: Boolean,
    @field:JsonInclude(JsonInclude.Include.NON_NULL) private val code: String? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL) private val data: T? = null,
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                true,
                null,
                data,
            )
        }

        fun fail(errorCode: ErrorCode): ApiResponse<Nothing> {
            return ApiResponse(false, errorCode.code, null)
        }
    }
}

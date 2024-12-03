package org.pofo.api.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.pofo.common.exception.ErrorCode

class ApiResponse<T> {
    @JsonProperty
    private val success: Boolean
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val code: String?
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T?

    constructor(data: T) {
        this.success = true
        this.data = data
        this.code = null
    }

    constructor(errorCode: ErrorCode) {
        this.success = false
        this.data = null
        this.code = errorCode.code
    }

    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(data)
        }

        fun failure(errorCode: ErrorCode): ApiResponse<Nothing> {
            return ApiResponse(errorCode)
        }
    }
}

package org.pofo.api.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import org.pofo.api.common.exception.ErrorCode

@Schema(description = "API 응답 모델")
class ApiResponse<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    @JsonProperty
    private val success: Boolean

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "에러 코드", example = "C001")
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
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(data)

        fun failure(errorCode: ErrorCode): ApiResponse<Nothing> = ApiResponse(errorCode)
    }
}

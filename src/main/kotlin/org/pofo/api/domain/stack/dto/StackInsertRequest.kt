package org.pofo.api.domain.stack.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class StackInsertRequest(
    @field:NotBlank(message = "스택 이름은 필수 입력 값입니다.")
    val name: String,
    @field:Pattern(
        regexp = "^https://[\\w\\-]+(\\.[\\w\\-]+)+([/#?].*)?$",
        message = "스택 이미지는 URL 형식이여야 하며, HTTPS 프로토콜을 사용해야합니다.",
    )
    val imageUrl: String?,
)

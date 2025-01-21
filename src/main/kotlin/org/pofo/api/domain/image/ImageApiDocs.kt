package org.pofo.api.domain.image

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pofo.api.common.response.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerResponse

@Tag(name = "[Image API]", description = "이미지 관련 API")
interface ImageApiDocs {
    @Operation(summary = "이미지 업로드", description = "이미지를 업로드 합니다. (개발중)")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "이미지 업로드 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "이미지 S3에 업로드 성공",
                                value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "id": 0,
                                        "email": "string"
                                      }
                                    }
                                """,
                            ),
                            ExampleObject(
                                name = "검증 성공 - 기존에 등록한 소셜 계정 없음 (스웨거 문서 제작 예시로 임시로 중복해서 넣었습니다)",
                                value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "id": 0,
                                        "email": "string"
                                      }
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun uploadImage(): ApiResponse<Map<String, String>>
}

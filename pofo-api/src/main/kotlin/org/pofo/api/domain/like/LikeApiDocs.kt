package org.pofo.api.domain.like

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pofo.api.common.response.ApiResponse
import org.pofo.api.domain.security.PrincipalDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerResponse

@Tag(name = "[Like API]", description = "좋아요 관련 API")
interface LikeApiDocs {
    @Operation(summary = "좋아요 등록", description = "특정 프로젝트에 좋아요를 등록합니다.")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "좋아요 등록 성공",
            ),
        ],
    )
    fun likeProject(
        @Parameter(hidden = true) @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable("projectId") projectId: Long,
    ): ApiResponse<Map<String, Int>>

    @Operation(summary = "좋아요 해제", description = "특정 프로젝트에 등록된 좋아요를 해제합니다.")
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "좋아요 해제 성공",
            ),
        ],
    )
    fun unlikeProject(
        @Parameter(hidden = true) @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable("projectId") projectId: Long,
    ): ApiResponse<Map<String, Int>>
}

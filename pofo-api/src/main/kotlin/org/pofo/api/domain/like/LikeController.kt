package org.pofo.api.domain.like

import org.pofo.api.common.response.ApiResponse
import org.pofo.api.domain.security.PrincipalDetails
import org.pofo.common.response.Version
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Version.V1 + "/likes")
class LikeController(
    private val likeService: LikeService,
) : LikeApiDocs {
    @PostMapping("/{projectId}")
    override fun likeProject(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable projectId: Long,
    ): ApiResponse<Unit> {
        likeService.likeProject(principalDetails.jwtTokenData.userId, projectId)
        return ApiResponse.success(Unit)
    }

    @DeleteMapping("/{projectId}")
    override fun unlikeProject(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable projectId: Long,
    ): ApiResponse<Unit> {
        likeService.unlikeProject(principalDetails.jwtTokenData.userId, projectId)
        return ApiResponse.success(Unit)
    }
}

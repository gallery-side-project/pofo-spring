package org.pofo.api.domain.like

import org.pofo.api.common.response.ApiResponse
import org.pofo.api.common.util.Version
import org.pofo.api.domain.security.PrincipalDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Version.V1 + "/like")
class LikeController(
    private val likeService: LikeService,
) : LikeApiDocs {
    @PostMapping("/{projectId}")
    override fun likeProject(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable projectId: Long,
    ): ApiResponse<Map<String, Int>> {
        val currentLikes = likeService.likeProject(principalDetails.jwtTokenData.userId, projectId)
        return ApiResponse.success(mapOf("likes" to currentLikes))
    }

    @DeleteMapping("/{projectId}")
    override fun unlikeProject(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @PathVariable projectId: Long,
    ): ApiResponse<Map<String, Int>> {
        val currentLikes = likeService.unlikeProject(principalDetails.jwtTokenData.userId, projectId)
        return ApiResponse.success(mapOf("likes" to currentLikes))
    }
}

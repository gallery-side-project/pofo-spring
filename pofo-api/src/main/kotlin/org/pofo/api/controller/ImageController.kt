package org.pofo.api.controller

import org.pofo.api.common.response.ApiResponse
import org.pofo.api.docs.ImageApiDocs
import org.pofo.common.response.Version
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Version.V1 + "/image")
class ImageController : ImageApiDocs {
    @PostMapping("")
    override fun uploadImage(): ApiResponse<Map<String, String>> {
        // TODO: Image Upload 로직 작성
        val tmp = "tmp"
        val responseData = mapOf("url" to tmp)
        return ApiResponse.success(responseData)
    }
}

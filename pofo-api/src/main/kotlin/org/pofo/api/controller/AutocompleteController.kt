@file:Suppress("ktlint:standard:no-wildcard-imports")

package org.pofo.api.controller

import org.pofo.api.common.response.ApiResponse
import org.pofo.api.docs.AutocompleteApiDocs
import org.pofo.api.service.OpenSearchService
import org.pofo.common.response.Version
import org.pofo.infra.elasticsearch.document.TechStackAutoComplete
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Version.V1 + "/tech-stack")
class AutocompleteController(
    private val openSearchService: OpenSearchService,
) : AutocompleteApiDocs {
    @PostMapping("/")
    override fun insertTechStack(
        @RequestBody techStack: TechStackAutoComplete,
    ): ApiResponse<String> {
        openSearchService.indexTechStack(techStack)
        return ApiResponse.success("단일 데이터 삽입 성공")
    }

    @PostMapping("/upload-csv")
    override fun uploadCSV(
        @RequestParam("file") file: MultipartFile,
    ): ApiResponse<String> {
        openSearchService.bulkInsertFromCSV(file)
        return ApiResponse.success("CSV 데이터 삽입 성공")
    }

    @GetMapping("/autocomplete")
    override fun autoComplete(
        @RequestParam query: String,
    ): ApiResponse<Map<String, List<String>>> {
        val suggestions = openSearchService.getSuggestions(query)
        val responseData = mapOf("autocomplete" to suggestions)
        return ApiResponse.success(responseData)
    }

    @Deprecated(message = "제거될 예정인 API 입니다")
    @GetMapping("/field")
    override fun searchSingleField(
        @RequestParam field: String,
        @RequestParam keyword: String,
    ): ApiResponse<List<TechStackAutoComplete>> =
        ApiResponse.success(openSearchService.searchSingleField(field, keyword))
}

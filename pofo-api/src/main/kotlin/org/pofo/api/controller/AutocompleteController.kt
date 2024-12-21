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
@RequestMapping("/tech-stack")
class AutocompleteController(
    private val openSearchService: OpenSearchService,
) : AutocompleteApiDocs {
    @PostMapping(Version.V1 + "/")
    override fun insertTechStack(
        @RequestBody techStack: TechStackAutoComplete,
    ): ApiResponse<String> {
        openSearchService.indexTechStack(techStack)
        return ApiResponse.success("단일 데이터 삽입 성공")
    }

    @PostMapping(Version.V1 + "/upload-csv")
    override fun uploadCSV(
        @RequestParam("file") file: MultipartFile,
    ): ApiResponse<String> {
        openSearchService.bulkInsertFromCSV(file)
        return ApiResponse.success("CSV 데이터 삽입 성공")
    }

    @GetMapping(Version.V1 + "/autocomplete")
    override fun autoComplete(
        @RequestParam query: String,
    ): ApiResponse<List<String>> = ApiResponse.success(openSearchService.getSuggestions(query))

    @GetMapping(Version.V1 + "/field")
    override fun searchSingleField(
        @RequestParam field: String,
        @RequestParam keyword: String,
    ): ApiResponse<List<TechStackAutoComplete>> =
        ApiResponse.success(openSearchService.searchSingleField(field, keyword))
}

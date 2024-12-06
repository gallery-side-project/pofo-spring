package org.pofo.api.controller

import org.pofo.api.common.response.ApiResponse
import org.pofo.api.service.OpenSearchService
import org.pofo.infra.elasticsearch.document.TechStackAutoComplete
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/tech-stack")
@RestController
class AutocompleteController(
    private val openSearchService: OpenSearchService,
) {
    @PostMapping("/")
    fun insertTechStack(
        @RequestBody techStack: TechStackAutoComplete,
    ): ApiResponse<String> {
        openSearchService.indexTechStack(techStack)
        return ApiResponse.success("단일 테이터 삽입 성공")
    }

    @PostMapping("/upload-csv")
    fun uploadCSV(
        @RequestParam("file") file: MultipartFile,
    ): ApiResponse<String> {
        openSearchService.bulkInsertFromCSV(file)
        return ApiResponse.success("CSV 데이터 삽입 성공")
    }

    @GetMapping("/autocomplete")
    fun autoComplete(
        @RequestParam query: String,
    ): ApiResponse<List<String>> = ApiResponse.success(openSearchService.getSuggestions(query))

    @GetMapping("/field")
    fun searchSingleField(
        @RequestParam field: String,
        @RequestParam keyword: String,
    ): ApiResponse<List<TechStackAutoComplete>> =
        ApiResponse.success(openSearchService.searchSingleField(field, keyword))
}

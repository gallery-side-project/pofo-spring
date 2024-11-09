package org.pofo.api.controller

import com.pofo.elasticsearch.document.TechStackAutoComplete
import org.pofo.api.service.OpenSearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/tech-stack")
@RestController
class AutocompleteController(
    private val openSearchService: OpenSearchService,
) {
    @PostMapping("/")
    fun insertTechStack(
        @RequestBody techStack: TechStackAutoComplete,
    ): ResponseEntity<String> =
        if (openSearchService.indexTechStack(techStack)) {
            ResponseEntity.ok("Data inserted successfully")
        } else {
            ResponseEntity.status(500).body("Failed to insert data")
        }

    @PostMapping("/upload-csv")
    fun uploadCSV(
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> =
        try {
            openSearchService.bulkInsertFromCSV(file)
            ResponseEntity.ok("CSV 데이터가 성공적으로 업로드되었습니다.")
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(400).body("CSV 업로드에 실패했습니다: ${e.message}")
        }

    @GetMapping("/autocomplete")
    fun autoComplete(
        @RequestParam query: String,
    ): ResponseEntity<List<String>> = ResponseEntity.ok(openSearchService.getSuggestions(query))

    @GetMapping("/field")
    fun searchSingleField(
        @RequestParam field: String,
        @RequestParam keyword: String,
    ): List<TechStackAutoComplete> = openSearchService.searchSingleField(field, keyword)
}

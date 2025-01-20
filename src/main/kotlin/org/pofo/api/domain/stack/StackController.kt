package org.pofo.api.domain.stack

import jakarta.validation.Valid
import org.pofo.api.common.response.ApiResponse
import org.pofo.api.common.util.Version
import org.pofo.api.domain.stack.dto.StackInsertRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Version.V1 + "/tech-stack")
class StackController(
    private val stackService: StackService,
) : StackApiDocs {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    override fun insertStack(
        @RequestBody @Valid stack: StackInsertRequest,
    ): ApiResponse<String> {
        stackService.insertStack(stack)
        return ApiResponse.success("단일 데이터 삽입 성공")
    }

    @PostMapping("/upload-csv")
    @ResponseStatus(HttpStatus.CREATED)
    override fun uploadCSV(
        @RequestParam("file") file: MultipartFile,
    ): ApiResponse<String> {
        val successCount = stackService.bulkInsertFromCSV(file)
        return ApiResponse
            .success("${successCount}개의 CSV 데이터 삽입 성공")
    }

    @GetMapping("/autocomplete")
    override fun autoComplete(
        @RequestParam(defaultValue = "") query: String,
    ): ApiResponse<List<String>> {
        val suggestions = stackService.getSuggestions(query)
        return ApiResponse.success(suggestions)
    }
}

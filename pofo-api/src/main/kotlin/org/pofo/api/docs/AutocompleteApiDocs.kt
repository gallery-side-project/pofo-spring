package org.pofo.api.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.pofo.api.common.response.ApiResponse
import org.pofo.infra.elasticsearch.document.TechStackAutoComplete
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Tag(name = "[Tech Stack API]", description = "기술 스택 관련 API")
interface AutocompleteApiDocs {
    @Operation(summary = "단일 기술 스택 삽입", description = "단일 기술 스택 데이터를 OpenSearch에 삽입합니다.")
    fun insertTechStack(
        @RequestBody techStack: TechStackAutoComplete,
    ): ApiResponse<String>

    @Operation(summary = "CSV 파일 업로드", description = "CSV 파일의 기술 스택 데이터를 OpenSearch에 대량 삽입합니다.")
    @Parameters(
        Parameter(name = "file", description = "업로드할 CSV 파일", required = true, `in` = ParameterIn.QUERY),
    )
    fun uploadCSV(
        @RequestParam("file") file: MultipartFile,
    ): ApiResponse<String>

    @Operation(summary = "자동완성 검색", description = "사용자가 입력한 키워드에 기반하여 자동완성 제안을 제공합니다.")
    @Parameters(
        Parameter(name = "query", description = "자동완성 검색어", required = true, `in` = ParameterIn.QUERY),
    )
    fun autoComplete(
        @RequestParam("query") query: String,
    ): ApiResponse<List<String>>

    @Operation(summary = "단일 필드 검색", description = "특정 필드와 키워드로 기술 스택 데이터를 검색합니다.")
    @Parameters(
        Parameter(name = "field", description = "검색할 필드명", required = true, `in` = ParameterIn.QUERY),
        Parameter(name = "keyword", description = "검색 키워드", required = true, `in` = ParameterIn.QUERY),
    )
    fun searchSingleField(
        @RequestParam("field") field: String,
        @RequestParam("keyword") keyword: String,
    ): ApiResponse<List<TechStackAutoComplete>>
}

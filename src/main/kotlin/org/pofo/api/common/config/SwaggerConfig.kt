package org.pofo.api.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.BooleanSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("local", "dev")
class SwaggerConfig {
    private val token = "JWT"

    @Bean
    fun operationCustomizer(): OperationCustomizer =
        OperationCustomizer { operation, _ ->
            operation.responses?.let { responses ->
                responses.forEach { (_, apiResponse) ->
                    apiResponse.content?.forEach { (_, mediaType) ->
                        mediaType.schema = wrapSchema(mediaType.schema)
                    }
                }
            }
            operation
        }

    private fun wrapSchema(originalSchema: Schema<*>?): Schema<*> {
        val wrapperSchema = ObjectSchema()

        wrapperSchema.addProperty("success", BooleanSchema().example(true))
        wrapperSchema.addProperty("code", StringSchema().example("C001"))
        wrapperSchema.addProperty("data", originalSchema)

        return wrapperSchema
    }

    // API 그룹화 필요시 추후 진행.
    // 지금은 API가 몇개 없어서 단일 그룹으로 헀는데 추후 그룹화 하는게 좋습니다.
    @Bean
    fun techStackApiGroup(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .packagesToScan("org.pofo.api.domain.stack")
            .group("Tech Stack Group")
            .build()

    @Bean
    fun userApiGroup(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .packagesToScan("org.pofo.api.domain.user")
            .group("User Group")
            .build()

    @Bean
    fun openAPI(): OpenAPI {
        val securityRequirement = SecurityRequirement().addList(token)

        return OpenAPI()
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .components(securitySchemes())
    }

    private fun securitySchemes(): Components {
        val securitySchemeAccessToken =
            SecurityScheme()
                .name(token)
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .`in`(SecurityScheme.In.HEADER)
                .name("Authorization")

        return Components().addSecuritySchemes(token, securitySchemeAccessToken)
    }

    private fun apiInfo(): Info =
        Info()
            .title("Pofo API Docs")
            .description("Pofo 플랫폼 API 명세서")
            .version("v1.0.0")
}

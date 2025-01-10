package org.pofo.api.domain.stack

import com.opencsv.CSVReader
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.opensearch._types.FieldValue
import org.opensearch.client.opensearch.core.BulkRequest
import org.opensearch.client.opensearch.core.IndexRequest
import org.opensearch.client.opensearch.core.IndexResponse
import org.opensearch.client.opensearch.core.SearchRequest
import org.opensearch.client.opensearch.core.SearchResponse
import org.opensearch.client.opensearch.core.bulk.CreateOperation
import org.opensearch.client.opensearch.core.search.FieldSuggester
import org.opensearch.client.opensearch.core.search.SourceConfig
import org.opensearch.client.opensearch.core.search.Suggester
import org.pofo.infra.elasticsearch.document.TechStackAutoComplete
import org.springframework.data.elasticsearch.core.suggest.Completion
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStreamReader

@Service
class StackService(
    private val openSearchClient: OpenSearchClient,
) {
    fun bulkInsertFromCSV(file: MultipartFile) {
        val indexName =
            "stack-auto-complete"

        val bulkRequest =
            BulkRequest
                .Builder()
        CSVReader(
            InputStreamReader(
                file.inputStream,
            ),
        ).use { csvReader ->
            val rows =
                csvReader
                    .readAll()
                    .drop(
                        1,
                    )

            rows
                .forEach { row ->
                    val stackName =
                        row[0]
                    val docId =
                        stackName
                            .replace(
                                " ",
                                "_",
                            ).lowercase()

                    val suggestInputs =
                        generateAllCombinations(
                            stackName,
                        )

                    val techStack =
                        TechStackAutoComplete
                            .builder()
                            .id(
                                docId,
                            ).suggest(
                                Completion(
                                    suggestInputs,
                                ),
                            ).name(
                                stackName,
                            ).build()

                    bulkRequest
                        .operations { operation ->
                            operation
                                .create { co: CreateOperation.Builder<TechStackAutoComplete> ->
                                    co
                                        .index(
                                            indexName,
                                        ).id(
                                            docId,
                                        ).document(
                                            techStack,
                                        )
                                }
                        }
                }
        }

        openSearchClient
            .bulk(
                bulkRequest
                    .build(),
            )
    }

    private fun generateAllCombinations(input: String): List<String> {
        val cleanedInput =
            input
                .replace(
                    Regex(
                        "[^A-Za-z0-9 ]",
                    ),
                    "",
                )
        val words =
            cleanedInput
                .split(
                    " ",
                )
        val combinations =
            mutableSetOf<String>()

        for (i in words.indices) {
            for (j in i until
                words.size) {
                combinations
                    .add(
                        words
                            .slice(
                                i..j,
                            ).joinToString(
                                " ",
                            ).lowercase(),
                    )
            }
        }

        return combinations
            .toList()
    }

    fun getSuggestions(query: String): List<String> {
        val indexName =
            "stack-auto-complete"
        val sanitizedQuery =
            query
                .lowercase()
                .replace(
                    " ",
                    "",
                )

        return try {
            val map =
                HashMap<String, FieldSuggester>()
            map["tech-suggestion"] =
                FieldSuggester.of { fs ->
                    fs
                        .completion { cs ->
                            cs
                                .skipDuplicates(
                                    true,
                                ).size(
                                    20,
                                ).field(
                                    "suggest",
                                )
                        }
                }

            val suggester =
                Suggester
                    .of { s ->
                        s
                            .suggesters(
                                map,
                            ).text(
                                sanitizedQuery,
                            )
                    }

            val searchRequest =
                SearchRequest
                    .of { search ->
                        search
                            .index(
                                indexName,
                            ).suggest(
                                suggester,
                            ).source(
                                SourceConfig
                                    .of { s ->
                                        s
                                            .filter { f ->
                                                f
                                                    .includes(
                                                        listOf(
                                                            "name",
                                                        ),
                                                    )
                                            }
                                    },
                            )
                    }

            val response =
                openSearchClient
                    .search(
                        searchRequest,
                        TechStackAutoComplete::class.java,
                    )

            response.suggest()["tech-suggestion"]?.flatMap { suggestion ->
                suggestion
                    .completion()
                    .options()
                    .map {
                        it
                            .source()
                            .name
                    }
            }
                ?: emptyList()
        } catch (
            e: Exception,
        ) {
            e
                .printStackTrace()
            emptyList()
        }
    }

    fun indexTechStack(techStack: TechStackAutoComplete): Boolean {
        val indexName =
            "stack-auto-complete"
        return try {
            val request =
                IndexRequest
                    .Builder<TechStackAutoComplete>()
                    .index(
                        indexName,
                    ).id(
                        techStack.id,
                    ).document(
                        techStack,
                    ).build()

            val response:
                IndexResponse =
                openSearchClient
                    .index(
                        request,
                    )
            response.result().name ==
                "created"
        } catch (
            e: Exception,
        ) {
            System.out
                .println(
                    e,
                )
            e
                .printStackTrace()
            false
        }
    }

    fun searchSingleField(
        fieldName: String,
        keyword: String,
    ): List<TechStackAutoComplete> =
        try {
            val indexName =
                "stack-auto-complete"

            val request =
                SearchRequest
                    .of { searchRequest ->
                        searchRequest
                            .index(
                                indexName,
                            ).query { query ->
                                query
                                    .match { matchQuery ->
                                        matchQuery
                                            .field(
                                                fieldName,
                                            )
                                        matchQuery
                                            .query(
                                                FieldValue
                                                    .of(
                                                        keyword,
                                                    ),
                                            )
                                        matchQuery
                                    }
                            }
                    }

            val response:
                SearchResponse<TechStackAutoComplete> =
                openSearchClient
                    .search(
                        request,
                        TechStackAutoComplete::class.java,
                    )

            response
                .hits()
                .hits()
                .mapNotNull {
                    it
                        .source()
                }
        } catch (
            e: Exception,
        ) {
            e
                .printStackTrace()
            emptyList()
        }
}

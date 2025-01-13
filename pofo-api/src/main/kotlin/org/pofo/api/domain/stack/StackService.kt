package org.pofo.api.domain.stack

import com.opencsv.CSVReader
import io.github.oshai.kotlinlogging.KotlinLogging
import org.pofo.api.domain.stack.dto.StackInsertRequest
import org.pofo.domain.rds.domain.project.Stack
import org.pofo.domain.rds.domain.project.repository.StackRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.InputStreamReader

@Service
@Primary
class StackService(
    private val stackRepository: StackRepository,
) {
    companion object {
        val logger = KotlinLogging.logger {}
    }

    @Transactional
    fun insertStack(stackInsertRequest: StackInsertRequest) {
        val stack =
            Stack
                .builder()
                .name(stackInsertRequest.name)
                .imageUrl(stackInsertRequest.imageUrl)
                .build()

        stackRepository.save(stack)
    }

    fun bulkInsertFromCSV(file: MultipartFile): Int {
        var successCount = 0
        var failedCount = 0

        file.inputStream.use { inputStream ->
            CSVReader(InputStreamReader(inputStream)).use { reader ->
                val rows =
                    reader
                        .readAll()
                        .drop(1)

                rows.forEach { row ->
                    val stackName = row[0]
                    val stackImageUrl = row[2]

                    val stack =
                        Stack
                            .builder()
                            .name(stackName)
                            .imageUrl(stackImageUrl)
                            .build()

                    runCatching {
                        stackRepository.save(stack)
                    }.onSuccess {
                        successCount++
                    }.onFailure {
                        failedCount++
                        logger.warn { "Failed to insert stack $stackName" }
                    }
                }
            }
        }

        logger.info { "bulkInsertFromCSV - success: $successCount / failed: $failedCount" }
        return successCount
    }

    @Transactional(readOnly = true)
    fun getSuggestions(query: String): List<String> {
        val fountStacks = stackRepository.findByNameContainingIgnoreCase(query)
        return fountStacks.map { it.name }
    }
}

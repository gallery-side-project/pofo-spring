package org.pofo.api.domain.stack

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.pofo.api.common.fixture.StackFixture
import org.pofo.api.domain.project.Stack
import org.pofo.api.domain.project.repository.StackRepository

internal class StackServiceTest :
    StringSpec({
        lateinit var stackService: StackService
        lateinit var stackRepository: StackRepository

        beforeEach {
            stackRepository = mockk<StackRepository>()
            stackService = StackService(stackRepository)
        }

        "bulkInsertFromCSV" {
            val stackList = StackFixture.DEFAULT_STACK_LIST
            val mockFile = StackFixture.createMockMultipartFile(stackList = stackList)

            every { stackRepository.save(any(Stack::class)) } returnsMany stackList

            val result = stackService.bulkInsertFromCSV(file = mockFile)

            result shouldBe 3
        }
    })

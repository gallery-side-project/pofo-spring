package org.pofo.api.common.fixture

import org.pofo.api.domain.project.Stack
import org.springframework.mock.web.MockMultipartFile

class StackFixture {
    companion object {
        val DEFAULT_STACK_LIST: List<Stack> =
            listOf(
                Stack(
                    1,
                    "JavaScript",
                    "https://img.stackshare.io/service/1209/javascript.jpeg",
                ),
                Stack(
                    2,
                    "Python",
                    "https://img.stackshare.io/service/993/pUBY5pVj.png",
                ),
                Stack(
                    3,
                    "Node.js",
                    "https://img.stackshare.io/service/1011/n1JRsFeB_400x400.png",
                ),
            )

        fun createMockMultipartFile(stackList: List<Stack> = DEFAULT_STACK_LIST): MockMultipartFile {
            val stringBuilder = StringBuilder()
            stringBuilder.append("stack_name,stack_type,image_url,homepage_url\n")
            stackList.forEach { stack ->
                stringBuilder.append("${stack.name},_,${stack.imageUrl},_\n")
            }
            return MockMultipartFile(
                "file",
                "stack.csv",
                "text/csv",
                (
                    stringBuilder.toString()
                ).toByteArray(),
            )
        }
    }
}

package org.pofo.api.common.exception

class CustomException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message) {
    override fun fillInStackTrace(): Throwable = this
}

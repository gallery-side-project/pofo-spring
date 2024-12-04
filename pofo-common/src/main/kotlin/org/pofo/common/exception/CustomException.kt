package org.pofo.common.exception

class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message) {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

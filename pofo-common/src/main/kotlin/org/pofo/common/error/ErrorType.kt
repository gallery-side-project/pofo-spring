package org.pofo.common.error

enum class ErrorType(val status: Int, val message: String) {
    // USER
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
    USER_EXISTS(400, "유저가 이미 존재합니다."),
    INVALID_PASSWORD(400, "유저의 패스워드가 일치하지 않습니다."),
    PROJECT_NOT_FOUND(404, "프로젝트를 찾을 수 없습니다."),
}

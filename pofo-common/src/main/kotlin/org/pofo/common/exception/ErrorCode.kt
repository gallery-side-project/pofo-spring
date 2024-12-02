package org.pofo.common.exception

enum class ErrorCode(val status: Int, val message: String, val code: String) {
    INTERNAL_SERVER_ERROR(500, "서버 오류", "COMMON-001"),
    UNAUTHORIZED(401, "인증 오류", "COMMON-002"),

    // USER
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다.", "USER-001"),
    USER_EXISTS(400, "유저가 이미 존재합니다.", "USER-002"),
    INVALID_PASSWORD(400, "유저의 패스워드가 일치하지 않습니다.", "USER-003"),

    // PROJECT
    PROJECT_NOT_FOUND(404, "프로젝트를 찾을 수 없습니다.", "PROJ-004"),
}

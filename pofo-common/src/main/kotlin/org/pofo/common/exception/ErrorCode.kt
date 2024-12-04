package org.pofo.common.exception

enum class ErrorCode(val status: Int, val message: String, val code: String) {
    INTERNAL_SERVER_ERROR(500, "서버 오류", "COMMON-001"),
    UNAUTHORIZED(401, "인증 오류", "COMMON-002"),
    FORBIDDEN(403, "인가 오류", "COMMON-003"),

    // USER
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다.", "USER-001"),
    USER_EXISTS(400, "유저가 이미 존재합니다.", "USER-002"),

    // PROJECT
    PROJECT_NOT_FOUND(404, "프로젝트를 찾을 수 없습니다.", "PROJ-004"),
}

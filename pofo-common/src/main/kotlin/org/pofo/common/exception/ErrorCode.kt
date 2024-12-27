package org.pofo.common.exception

enum class ErrorCode(
    val status: Int,
    val message: String,
    val code: String,
) {
    INTERNAL_SERVER_ERROR(500, "서버 오류", "COMMON-001"),
    UNAUTHORIZED(401, "인증 오류", "COMMON-002"),
    FORBIDDEN(403, "인가 오류", "COMMON-003"),
    NOT_FOUND(404, "잘못된 URL 호출", "COMMON-004"),
    METHOD_NOT_ALLOWED(405, "허용되지 않는 메소드", "COMMON-005"),
    UNSUPPORTED_MEDIA_TYPE(415, "지원하지 않는 Content 형식", "COMMON-006"),

    // USER
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다.", "USER-001"),
    USER_EXISTS(400, "유저가 이미 존재합니다.", "USER-002"),
    USER_LOGIN_FAILED(401, "아이디 또는 비밀번호가 잘못되었습니다.", "USER-003"),

    // PROJECT
    PROJECT_NOT_FOUND(404, "프로젝트를 찾을 수 없습니다.", "PROJ-001"),
    PROJECT_STACK_NOT_FOUND(400, "프로젝트 스택을 찾을 수 없습니다.", "PROJ-002"),
    PROJECT_IMAGE_INDEX_ERROR(400, "대표 이미지 인덱스가 잘못되었습니다.", "PROJ-003"),
}

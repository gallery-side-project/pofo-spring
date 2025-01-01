package org.pofo.api.domain.user.dto

data class UserLoginRequest(
    val email: String,
    val password: String,
)

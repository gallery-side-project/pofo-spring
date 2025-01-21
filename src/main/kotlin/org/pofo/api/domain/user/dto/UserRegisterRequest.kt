package org.pofo.api.domain.user.dto

data class UserRegisterRequest(
    val email: String,
    val password: String,
    val username: String,
)

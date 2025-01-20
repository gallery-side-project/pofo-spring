package org.pofo.api.domain.user.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class TokenResponse(
    val accessToken: String,
    @JsonIgnore
    val refreshToken: String,
)

package org.pofo.api.domain.security.token

abstract class Token(
    val userId: Long,
    val value: String,
    val expiration: Long,
)

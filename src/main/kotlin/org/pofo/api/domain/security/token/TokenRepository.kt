package org.pofo.api.domain.security.token

interface TokenRepository<T : Token> {
    fun save(token: T)

    fun findByUserIdOrNull(userId: Long): String?

    fun deleteByUserId(userId: Long)
}

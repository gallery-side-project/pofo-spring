package org.pofo.api.domain.security.token

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.fixedRateTimer

class InMemoryTokenRepository<T : Token> : TokenRepository<T> {
    private val store = ConcurrentHashMap<Long, String>()
    private val ttlMap = ConcurrentHashMap<Long, Long>()

    init {
        fixedRateTimer("cleanUp", initialDelay = 0, period = 5 * 60 * 1000) {
            cleanUpExpiredTokens()
        }
    }

    override fun save(token: T) {
        store[token.userId] = token.value
        ttlMap[token.userId] =
            Instant.now().toEpochMilli() + token.expiration
    }

    override fun findByUserIdOrNull(userId: Long): String? {
        val ttl = ttlMap[userId]
        if (ttl != null && ttl < Instant.now().toEpochMilli()) {
            store.remove(userId)
            ttlMap.remove(userId)
            return null
        }
        return store[userId]
    }

    override fun deleteByUserId(userId: Long) {
        store.remove(userId)
        ttlMap.remove(userId)
    }

    private fun cleanUpExpiredTokens() {
        val now = Instant.now().toEpochMilli()
        ttlMap.entries.removeIf { (userId, expirationTime) ->
            if (expirationTime < now) {
                store.remove(userId)
                true
            } else {
                false
            }
        }
    }
}

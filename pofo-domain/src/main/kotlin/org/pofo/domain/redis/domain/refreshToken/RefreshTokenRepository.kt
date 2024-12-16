package org.pofo.domain.redis.domain.refreshToken

import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, Long>

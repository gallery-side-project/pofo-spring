package org.pofo.domain.redis.domain.accessToken

import org.springframework.data.repository.CrudRepository

interface BannedAccessTokenRepository : CrudRepository<BannedAccessToken, Long>

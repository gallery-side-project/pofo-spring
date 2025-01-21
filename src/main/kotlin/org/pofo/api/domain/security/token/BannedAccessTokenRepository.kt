package org.pofo.api.domain.security.token

import org.springframework.data.repository.CrudRepository

interface BannedAccessTokenRepository : CrudRepository<BannedAccessToken, Long>

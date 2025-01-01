package org.pofo.api.domain.security.jwt

import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.rds.domain.user.UserRole

data class JwtTokenData(
    val userId: Long,
    val email: String,
    val name: String,
    val role: UserRole,
    val imageUrl: String? = null,
) {
    companion object {
        const val KEY_EMAIL = "email"
        const val KEY_NAME = "name"
        const val KEY_ROLE = "role"
        const val KEY_IMAGE_URL = "username"
    }

    constructor(user: User) : this(user.id, user.email, "name", user.role)

    fun toMap(): Map<String, String> {
        val map =
            mutableMapOf(
                KEY_EMAIL to email,
                KEY_NAME to name,
                KEY_ROLE to role.name,
            )
        imageUrl?.let { map[KEY_IMAGE_URL] = it }
        return map
    }
}

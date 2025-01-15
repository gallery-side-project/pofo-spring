package org.pofo.api.common.fixture

import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.rds.domain.user.UserRole

class UserFixture {
    companion object {
        fun createUser(
            email: String = "test@org.com",
            password: String = "Test Password",
            username: String = "test Username",
            role: UserRole = UserRole.ROLE_USER,
        ): User =
            User
                .builder()
                .email(email)
                .password(password)
                .username(username)
                .role(role)
                .build()
    }
}

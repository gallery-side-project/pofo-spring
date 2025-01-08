package org.pofo.api.fixture

import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.rds.domain.user.UserRole

class UserFixture {
    companion object {
        private const val TEST_USER_EMAIL = "test@org.com"
        private const val TEST_USER_PASSWORD = "testPassword"
        private const val TEST_USERNAME = "testUsername"

        fun createUser(): User =
            this.createUser(
                email = TEST_USER_EMAIL,
                password = TEST_USER_PASSWORD,
                username = TEST_USERNAME,
                role = UserRole.ROLE_USER,
            )

        fun createUser(role: UserRole): User =
            this.createUser(
                email = TEST_USER_EMAIL,
                password = TEST_USER_PASSWORD,
                username = TEST_USERNAME,
                role = role,
            )

        fun createUser(
            email: String,
            password: String,
            username: String,
            role: UserRole,
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

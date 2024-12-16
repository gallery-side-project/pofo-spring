package org.pofo.api.fixture

import org.pofo.domain.rds.domain.user.User
import org.pofo.domain.rds.domain.user.UserRole

class UserFixture {
    companion object {
        private const val TEST_USER_EMAIL = "test@org.com"
        private const val TEST_USER_PASSWORD = "testPassword"

        fun createUser(): User = this.createUser(TEST_USER_EMAIL, TEST_USER_PASSWORD, UserRole.ROLE_USER)

        fun createUser(role: UserRole): User = this.createUser(TEST_USER_EMAIL, TEST_USER_PASSWORD, role)

        fun createUser(
            email: String,
            password: String,
            role: UserRole,
        ): User =
            User
                .builder()
                .email(email)
                .password(password)
                .role(role)
                .build()
    }
}

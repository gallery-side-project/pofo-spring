package org.pofo.domain.domain.user;

import org.springframework.lang.NonNull;

public enum UserRole {
    ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");

    @NonNull public final String name;

    UserRole(@NonNull String name) {
        this.name = name;
    }
}

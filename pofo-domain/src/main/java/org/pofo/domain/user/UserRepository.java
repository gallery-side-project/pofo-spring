package org.pofo.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Nullable
    User findByEmail(String email);

    @Nullable
    User findById(long id);
}
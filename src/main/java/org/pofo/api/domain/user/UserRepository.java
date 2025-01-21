package org.pofo.api.domain.user;

import org.pofo.api.common.annotation.NonNull;
import org.pofo.api.common.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Nullable
    User findByEmail(@NonNull String email);

    @Nullable
    @Query("SELECT u from UserSocialAccount usa " +
            "JOIN usa.user u " +
            "WHERE usa.socialAccountId = :socialAccountId AND usa.socialType = :socialType")
    User findBySocialAccountIdAntType(@NonNull @Param("socialAccountId") String socialAccountId, @NonNull @Param("socialType") UserSocialType socialType);

    boolean existsByEmailOrUsername(String email, String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}

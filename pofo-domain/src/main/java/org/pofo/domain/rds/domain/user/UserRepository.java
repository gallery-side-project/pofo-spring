package org.pofo.domain.rds.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

public interface UserRepository extends JpaRepository<User, Long> {

    @Nullable
    User findByEmail(String email);

    @Nullable
    User findById(long id);

    @Nullable
    @Query("SELECT u from UserSocialAccount usa " +
            "JOIN usa.user u " +
            "WHERE usa.socialAccountId = :socialAccountId AND usa.socialType = :socialType")
    User findBySocialAccountIdAntType(@Param("socialAccountId") String socialAccountId, @Param("socialType") UserSocialType socialType);

    boolean existsByEmailOrUsername(String email, String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}

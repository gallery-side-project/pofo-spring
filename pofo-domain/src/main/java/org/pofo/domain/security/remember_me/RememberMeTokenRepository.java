package org.pofo.domain.security.remember_me;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, Long> {
    @Nullable
    RememberMeToken findBySeries(String series);

    @Transactional
    void removeByEmail(String email);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE RememberMeToken t " +
                    "SET t.tokenValue = :tokenValue, t.lastUsedAt = :lastUsedAt " +
                    "WHERE t.series = :series"
    )
    void updateToken(@Param("series") String series, @Param("tokenValue") String tokenValue, @Param("lastUsedAt") Date lastUsedAt);
}

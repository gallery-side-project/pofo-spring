package org.pofo.domain.rds.domain.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface SessionPersistentRepository extends JpaRepository<SessionPersistent, Long> {
    @Nullable
    SessionPersistent findBySeries(String series);

    @Transactional
    void removeByEmail(String email);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE SessionPersistent t " +
                    "SET t.secret = :secret, t.lastUsedAt = :lastUsedAt " +
                    "WHERE t.series = :series"
    )
    void updateValueBySeries(@Param("series") String series, @Param("secret") String secret, @Param("lastUsedAt") Date lastUsedAt);
}


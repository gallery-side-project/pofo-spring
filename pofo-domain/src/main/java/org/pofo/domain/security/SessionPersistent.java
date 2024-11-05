package org.pofo.domain.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Date;

@Entity
@Table(name = "session_persistent")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SessionPersistent {
    @Id
    @EqualsAndHashCode.Include
    private String series;

    @NonNull
    @Column(nullable = false)
    private String secret;

    @NonNull
    @Column(nullable = false)
    private String email;

    @NonNull
    @Column(nullable = false)
    private Date lastUsedAt;

    private SessionPersistent(String series, @NonNull String email, @NonNull String secret, @NonNull Date lastUsedAt) {
        this.series = series;
        this.email = email;
        this.secret = secret;
        this.lastUsedAt = lastUsedAt;
    }

    public static SessionPersistent create(String email, String series, String token) {
        return new SessionPersistent(series, email, token, new Date());
    }

    public SessionPersistent updateValue(String value) {
        return new SessionPersistent(series, email, value, new Date());
    }
}

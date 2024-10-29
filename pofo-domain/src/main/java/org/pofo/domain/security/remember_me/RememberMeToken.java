package org.pofo.domain.security.remember_me;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import java.util.Date;

@Entity
@Table(name = "remember_me_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RememberMeToken {
    @Id
    @EqualsAndHashCode.Include
    private String series;

    @NonNull
    @Column(nullable = false)
    private String tokenValue;

    @NonNull
    @Column(nullable = false)
    private String email;

    @NonNull
    @Column(nullable = false)
    private Date lastUsedAt;

    private RememberMeToken(String series, @NotNull String email, @NotNull String tokenValue, @NotNull Date lastUsedAt) {
        this.series = series;
        this.email = email;
        this.tokenValue = tokenValue;
        this.lastUsedAt = lastUsedAt;
    }

    public static RememberMeToken create(String series, String email, String token, Date lastUsedAt) {
        return new RememberMeToken(series, email, token, lastUsedAt);
    }

    public RememberMeToken updateTokenValue(String newTokenValue) {
        return new RememberMeToken(series, email, newTokenValue, new Date());
    }
}

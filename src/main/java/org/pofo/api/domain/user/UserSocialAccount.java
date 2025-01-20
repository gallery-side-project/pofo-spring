package org.pofo.api.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.pofo.api.common.annotation.NonNull;

@Entity
@Table(name = "user_social_account")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialAccount {
    @Id
    private String socialAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(nullable = false)
    private UserSocialType socialType;

    @NonNull
    @Column(nullable = false)
    private String accessToken;
}

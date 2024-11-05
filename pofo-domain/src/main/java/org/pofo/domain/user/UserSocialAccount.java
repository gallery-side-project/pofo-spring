package org.pofo.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_social_account")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialAccount {
    @Id
    private String socialAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserSocialType socialType;

    @Column(nullable = false)
    private String accessToken;
}

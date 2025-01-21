package org.pofo.api.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pofo.api.common.annotation.NonNull;
import org.pofo.api.common.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"user\"")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @NonNull
    @Column(nullable = false)
    private String password;

    @NonNull
    @Column(nullable = false, unique = true)
    private String username;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    @Nullable
    @Column
    private String avatarUrl;
}

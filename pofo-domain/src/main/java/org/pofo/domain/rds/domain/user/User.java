package org.pofo.domain.rds.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

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

    @Column
    @NonNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    @Column
    private String avatarUrl;
}

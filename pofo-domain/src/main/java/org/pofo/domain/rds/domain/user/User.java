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

    @Column
    @NonNull
    private String email;

    @JsonIgnore
    @Column
    @NonNull
    private String password;

    @Column
    @NonNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;

    @Column
    private String avatarUrl;
}

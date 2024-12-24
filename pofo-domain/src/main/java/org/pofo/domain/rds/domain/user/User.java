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
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private User(@NonNull String email, @NonNull String password, @NonNull UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static User create(@NonNull String email, @NonNull String password) {
        return new User(email, password, UserRole.ROLE_USER);
    }
}

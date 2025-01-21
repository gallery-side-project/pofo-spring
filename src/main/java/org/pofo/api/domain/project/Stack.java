package org.pofo.api.domain.project;

import jakarta.persistence.*;
import lombok.*;

import javax.annotation.Nullable;

@Entity
@Table(name = "stack")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Stack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    @Nullable
    @Column
    private String imageUrl;
}

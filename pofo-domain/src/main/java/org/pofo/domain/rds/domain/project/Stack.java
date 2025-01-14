package org.pofo.domain.rds.domain.project;

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

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    @Nullable
    private String imageUrl;
}

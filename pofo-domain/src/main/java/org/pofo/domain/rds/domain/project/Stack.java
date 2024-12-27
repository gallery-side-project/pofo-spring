package org.pofo.domain.rds.domain.project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stack")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Stack(String name) {
        this.name = name;
    }
}

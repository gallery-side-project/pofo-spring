package org.pofo.domain.rds.domain.project;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_stack")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProjectStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "stack_id")
    private Stack stack;
}

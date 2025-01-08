package org.pofo.domain.rds.domain.like;

import jakarta.persistence.*;
import lombok.*;
import org.pofo.domain.rds.domain.project.Project;
import org.pofo.domain.rds.domain.user.User;

@Entity
@Table(name = "like")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}

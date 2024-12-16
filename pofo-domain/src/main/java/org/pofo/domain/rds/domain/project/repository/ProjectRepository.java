package org.pofo.domain.rds.domain.project.repository;

import jakarta.annotation.Nullable;
import org.pofo.domain.rds.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectCustomRepository {
    @Nullable
    Project findById(long id);
}

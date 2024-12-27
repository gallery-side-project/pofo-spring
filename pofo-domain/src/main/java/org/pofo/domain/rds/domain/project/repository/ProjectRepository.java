package org.pofo.domain.rds.domain.project.repository;

import jakarta.annotation.Nullable;
import org.pofo.domain.rds.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectCustomRepository {
    @Nullable
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.stacks WHERE p.id = :id")
    Project findById(long id);
}

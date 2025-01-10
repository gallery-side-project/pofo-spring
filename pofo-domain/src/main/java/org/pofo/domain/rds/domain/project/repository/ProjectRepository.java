package org.pofo.domain.rds.domain.project.repository;


import jakarta.annotation.Nullable;
import jakarta.persistence.LockModeType;
import org.pofo.domain.rds.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectCustomRepository {
    @Lock(LockModeType.OPTIMISTIC)
    @Nullable
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.stacks WHERE p.id = :id")
    Project findById(@Param("id") long id);
}

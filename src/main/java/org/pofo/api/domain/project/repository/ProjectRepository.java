package org.pofo.api.domain.project.repository;


import jakarta.persistence.LockModeType;
import org.pofo.api.common.annotation.NonNull;
import org.pofo.api.common.annotation.Nullable;
import org.pofo.api.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectCustomRepository {
    @Nullable
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Project p WHERE p.id = :projectId")
    Project findByIdOrNull(@NonNull @Param("projectId") Long projectId);
}

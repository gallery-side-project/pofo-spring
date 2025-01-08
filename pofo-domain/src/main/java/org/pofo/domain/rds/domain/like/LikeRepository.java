package org.pofo.domain.rds.domain.like;

import org.springframework.data.repository.query.Param;

import jakarta.annotation.Nullable;
import org.pofo.domain.rds.domain.project.Project;
import org.pofo.domain.rds.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndProject(User user, Project project);

    @Nullable
    Like findByUserAndProject(User user, Project project);

    @Query("SELECT l.project FROM Like l WHERE l.user.id = :userId")
    List<Project> findLikedProjectsByUserId(@Param("userId") Long userId);
}

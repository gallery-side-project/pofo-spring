package org.pofo.domain.rds.domain.like;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.catalina.User;
import org.pofo.domain.rds.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndProject(User user, Project project);

    Optional<Like> findByUserAndProject(User user, Project project);

    @Query("SELECT l.project FROM Like l WHERE l.user = :user")
    List<Project> findLikedProjectsByUser(@Param("user") User user);
}

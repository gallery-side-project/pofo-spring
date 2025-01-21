package org.pofo.api.domain.like;

import org.pofo.api.common.annotation.NonNull;
import org.pofo.api.common.annotation.Nullable;
import org.pofo.api.domain.project.Project;
import org.pofo.api.domain.user.User;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndProject(@NonNull User user, @NonNull Project project);

    @Nullable
    Like findByUserAndProject(@NonNull User user, @NonNull Project project);

    @NonNull
    @Query("SELECT l.project FROM Like l WHERE l.user.id = :userId")
    List<Project> findLikedProjectsByUserId(@Param("userId") Long userId);
}

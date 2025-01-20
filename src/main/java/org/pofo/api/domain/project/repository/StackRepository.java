package org.pofo.api.domain.project.repository;

import org.pofo.api.common.annotation.NonNull;
import org.pofo.api.domain.project.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StackRepository extends JpaRepository<Stack, Long> {
    @NonNull
    List<Stack> findByNameIn(List<String> names);
    @NonNull
    List<Stack> findByNameContainingIgnoreCase(@NonNull String name);
}

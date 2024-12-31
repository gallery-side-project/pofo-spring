package org.pofo.domain.rds.domain.project.repository;

import org.pofo.domain.rds.domain.project.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface StackRepository extends JpaRepository<Stack, Long> {
    List<Stack> findByNameIn(Collection<String> names);
}

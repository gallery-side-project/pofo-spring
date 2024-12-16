package org.pofo.domain.rds.domain.project.repository;

import org.pofo.domain.rds.domain.project.ProjectList;

public interface ProjectCustomRepository {
    ProjectList searchProjecWithCursor(int size, Long cursor);
}

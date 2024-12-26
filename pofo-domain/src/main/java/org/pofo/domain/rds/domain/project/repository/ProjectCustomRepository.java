package org.pofo.domain.rds.domain.project.repository;

import org.pofo.domain.rds.domain.project.ProjectList;

public interface ProjectCustomRepository {
    ProjectList searchProjectWithCursor(int size, Long cursor);
}

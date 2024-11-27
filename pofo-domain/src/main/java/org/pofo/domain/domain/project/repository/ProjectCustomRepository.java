package org.pofo.domain.domain.project.repository;

import org.pofo.domain.domain.project.ProjectList;

public interface ProjectCustomRepository {
    ProjectList searchProjecWithCursor(int size, Long cursor);
}

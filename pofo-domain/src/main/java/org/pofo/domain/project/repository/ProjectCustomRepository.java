package org.pofo.domain.project.repository;

import org.pofo.domain.project.ProjectList;

public interface ProjectCustomRepository {
    ProjectList searchProjecWithCursor(int size, Long cursor);
}

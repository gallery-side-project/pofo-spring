package org.pofo.domain.rds.domain.project.repository;

import org.pofo.domain.rds.domain.project.ProjectList;
import org.pofo.domain.rds.domain.project.vo.ProjectQuery;

public interface ProjectCustomRepository {
    ProjectList searchProjectWithCursor(int size, Long cursor);
    ProjectList searchProjectWithQuery(ProjectQuery query);
}

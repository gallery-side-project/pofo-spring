package org.pofo.domain.rds.domain.project.repository;

import org.pofo.domain.rds.domain.project.Project;
import org.pofo.domain.rds.domain.project.ProjectCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ProjectCustomRepository {
    Slice<Project> searchProjectWithCursor(int size, Long cursor);
    Slice<Project> searchProjectWithQuery(
            String title,
            ProjectCategory category,
            List<String> stackNames,
            Pageable pageable
    );
}

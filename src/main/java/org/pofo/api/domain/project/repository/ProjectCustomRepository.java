package org.pofo.api.domain.project.repository;

import org.pofo.api.domain.project.Category;
import org.pofo.api.domain.project.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ProjectCustomRepository {
    Slice<Project> searchProjectWithCursor(int size, long cursor);
    Slice<Project> searchProjectWithQuery(
            String title,
            List<Category> categories,
            List<String> stackNames,
            String authorName,
            Pageable pageable
    );
}

package org.pofo.domain.rds.domain.project.vo;

import org.pofo.domain.rds.domain.project.ProjectCategory;

import java.util.List;

public record ProjectQuery(
        String title,
        ProjectCategory category,
        List<String> stacks,
        Long cursorId,
        int size
) {
}

package org.pofo.domain.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProjectList {
    private List<Project> projects;
    private boolean hasNext;
    private int projectCount;
}

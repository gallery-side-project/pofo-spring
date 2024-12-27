package org.pofo.domain.rds.domain.project.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pofo.domain.rds.domain.project.Project;
import org.pofo.domain.rds.domain.project.ProjectList;
import org.pofo.domain.rds.domain.project.QProject;
import org.pofo.domain.rds.domain.project.vo.ProjectQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectCustomRepositoryImpl implements ProjectCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public ProjectList searchProjectWithCursor(int size, Long cursor) {
        QProject project = QProject.project;
        BooleanExpression predicate = null;

        if (cursor != null) {
            predicate = project.id.lt(cursor);
        }

        List<Project> projects = queryFactory.selectFrom(project)
                .where(predicate)
                .orderBy(project.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = projects.size() > size;

        if (hasNext) {
            projects.remove(size);
        }

        return new ProjectList(projects, hasNext, projects.size());
    }

    @Override
    public ProjectList searchProjectWithQuery(ProjectQuery query) {
        QProject qProject = QProject.project;
        BooleanExpression predicate = qProject.isNotNull();

        if (query.title() != null) {
            predicate = predicate.and(qProject.title.startsWith(query.title()));
        }

        if (query.category() != null) {
            predicate = predicate.and(qProject.category.eq(query.category()));
        }

        if (query.stacks() != null && !query.stacks().isEmpty()) {
            predicate = predicate.and(qProject.stacks.any().stack.name.in(query.stacks()));
        }

        List<Project> fetched = queryFactory.selectFrom(qProject)
                .where(predicate)
                .orderBy(qProject.id.desc())
                .limit(query.size() + 1)
                .fetch();

        boolean hasNext = fetched.size() > query.size();
        if (hasNext) {
            fetched.remove(query.size());
        }

        return new ProjectList(fetched, hasNext, fetched.size());
    }
}


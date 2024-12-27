package org.pofo.domain.rds.domain.project.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pofo.domain.rds.domain.project.Project;
import org.pofo.domain.rds.domain.project.ProjectList;
import org.pofo.domain.rds.domain.project.QProject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectCustomRepositoryImpl implements ProjectCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public ProjectList searchProjectWithCursor(int size, Long cursor) {
        QProject project = QProject.project;

        boolean isInitialRequest = (cursor == null);

        // 초기 요청: ID가 가장 큰 데이터부터 시작
        List<Project> projects = queryFactory.selectFrom(project)
                .where(isInitialRequest ? null : project.id.lt(cursor))
                .orderBy(project.id.desc()) // 항상 ID 내림차순 정렬
                .limit(size + 1) // 요청 크기 + 1로 가져와서 다음 페이지 확인
                .fetch();

        boolean hasNext = projects.size() > size;

        if (hasNext) {
            projects.remove(size);
        }

        return new ProjectList(projects, hasNext, projects.size());
    }
}

package org.pofo.domain.rds.domain.project.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pofo.domain.rds.domain.project.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectCustomRepositoryImpl implements ProjectCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Project> searchProjectWithCursor(int size, Long cursor) {
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

        return new SliceImpl<>(projects, PageRequest.ofSize(size), hasNext);
    }

    @Override
    public Slice<Project> searchProjectWithQuery(
            String title,
            List<Category> categories,
            List<String> stackNames,
            Pageable pageable) {
        QProject qProject = QProject.project;
        QProjectStack qProjectStack = QProjectStack.projectStack;
        BooleanExpression predicate = qProject.isNotNull();
        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();

        if (title != null) {
            predicate = predicate.and(qProject.title.startsWith(title));
        }

        if (categories != null && !categories.isEmpty()) {
            BooleanExpression categoriesCondition = categories.stream()
                    .map(category -> qProject.categories.any().category.eq(category))
                    .reduce(BooleanExpression::and)
                    .get();
            predicate = predicate.and(categoriesCondition);
        }

        if (stackNames != null && !stackNames.isEmpty()) {
            BooleanExpression stacksCondition = stackNames.stream()
                    .map(stackName -> qProject.stacks.any().stack.name.eq(stackName))
                    .reduce(BooleanExpression::and)
                    .get();
            predicate = predicate.and(stacksCondition);
        }

        List<Project> fetchedProjects = queryFactory.selectFrom(qProject)
                .where(predicate)
                .offset(offset)
                .limit(pageSize + 1)
                .orderBy(qProject.id.desc())
                .fetch();

        boolean hasNext = fetchedProjects.size() > pageSize;
        if (hasNext) {
            fetchedProjects.remove(pageSize);
        }

        fetchedProjects.forEach(project -> {
            List<ProjectStack> stacks = queryFactory.selectFrom(qProjectStack)
                    .where(qProjectStack.project.id.eq(project.getId()))
                    .fetch();
            project.setStacks(stacks);
        });

        return new SliceImpl<>(fetchedProjects, pageable, hasNext);
    }
}


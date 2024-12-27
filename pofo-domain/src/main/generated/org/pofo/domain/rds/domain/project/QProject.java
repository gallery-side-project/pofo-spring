package org.pofo.domain.rds.domain.project;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 1416526933L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final org.pofo.domain.rds.domain.user.QUser author;

    public final StringPath Bio = createString("Bio");

    public final EnumPath<ProjectCategory> category = createEnum("category", ProjectCategory.class);

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> imageUrls = this.<String, StringPath>createList("imageUrls", String.class, StringPath.class, PathInits.DIRECT2);

    public final BooleanPath isApproved = createBoolean("isApproved");

    public final NumberPath<Integer> keyImageIndex = createNumber("keyImageIndex", Integer.class);

    public final NumberPath<Long> likes = createNumber("likes", Long.class);

    public final ListPath<ProjectStack, EnumPath<ProjectStack>> stacks = this.<ProjectStack, EnumPath<ProjectStack>>createList("stacks", ProjectStack.class, EnumPath.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final ListPath<String, StringPath> urls = this.<String, StringPath>createList("urls", String.class, StringPath.class, PathInits.DIRECT2);

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProject(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProject(PathMetadata metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new org.pofo.domain.rds.domain.user.QUser(forProperty("author")) : null;
    }

}


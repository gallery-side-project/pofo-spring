package org.pofo.domain.rds.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserSocialAccount is a Querydsl query type for UserSocialAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserSocialAccount extends EntityPathBase<UserSocialAccount> {

    private static final long serialVersionUID = 1802382369L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserSocialAccount userSocialAccount = new QUserSocialAccount("userSocialAccount");

    public final StringPath accessToken = createString("accessToken");

    public final StringPath socialAccountId = createString("socialAccountId");

    public final EnumPath<UserSocialType> socialType = createEnum("socialType", UserSocialType.class);

    public final QUser user;

    public QUserSocialAccount(String variable) {
        this(UserSocialAccount.class, forVariable(variable), INITS);
    }

    public QUserSocialAccount(Path<? extends UserSocialAccount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserSocialAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserSocialAccount(PathMetadata metadata, PathInits inits) {
        this(UserSocialAccount.class, metadata, inits);
    }

    public QUserSocialAccount(Class<? extends UserSocialAccount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}


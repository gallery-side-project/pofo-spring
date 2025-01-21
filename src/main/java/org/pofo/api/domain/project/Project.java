package org.pofo.api.domain.project;

import jakarta.persistence.*;
import lombok.*;
import org.pofo.api.common.annotation.Nullable;
import org.pofo.api.common.annotation.NonNull;
import org.pofo.api.common.converter.StringListConverter;
import org.pofo.api.domain.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Version
    private Integer version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String title;

    @Nullable
    @Column
    private String bio; // 한줄 소개

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Integer keyImageIndex = -1; // 대표 이미지 인덱스

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Integer likes = 0; //좋아요 수 (매번 집계 쿼리 사용을 피하기 위함)

    @Convert(converter = StringListConverter.class)
    @Nullable
    @Column(columnDefinition = "TEXT")
    private List<String> urls; // 유저가 설정한 url list ex) github, npm 등등

    @Convert(converter = StringListConverter.class)
    @Nullable
    @Column(columnDefinition = "TEXT")
    private List<String> imageUrls;

    @NonNull
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean isApproved = false; // 모음팀 측에서 인증됬는지 (타 앱 연동을 통해)

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCategory> categories = new ArrayList<>(); // 프로젝트 유형

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectStack> stacks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    public void addStack(Stack stack) {
        ProjectStack projectStack = ProjectStack.builder()
            .project(this)
            .stack(stack)
            .build();
        this.stacks.add(projectStack);
    }

    public void updateStack(List<Stack> stacks) {
        this.stacks.clear();
        for (Stack stack : stacks) {
            this.addStack(stack);
        }
    }

    public void addCategory(Category category) {
        ProjectCategory projectCategory = ProjectCategory.builder()
            .project(this)
            .category(category)
            .build();
        this.categories.add(projectCategory);
    }

    public void updateCategories(List<Category> categories) {
        this.categories.clear();
        for (Category category : categories) {
            this.addCategory(category);
        }
    }

    public Project update(String title, String bio, List<String> urls, List<String> imageUrls, Integer keyImageIndex, String content) {
        if (title != null) {
            this.title = title;
        }
        if (bio != null) {
            this.bio = bio;
        }
        if (urls != null) {
            this.urls = urls;
        }
        if (keyImageIndex != null) {
            this.keyImageIndex = keyImageIndex;
        }
        if (imageUrls != null) {
            this.imageUrls = imageUrls;
        }
        if (content != null) {
            this.content = content;
        }
        return this;
    }

    public void increaseLikes() {
        this.likes++;
    }

    public void decreaseLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}

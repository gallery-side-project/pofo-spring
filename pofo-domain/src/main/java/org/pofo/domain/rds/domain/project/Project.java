package org.pofo.domain.rds.domain.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pofo.domain.rds.converter.StringListConverter;
import org.pofo.domain.rds.domain.user.User;

import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String Bio; // 한줄 소개

    @Column
    @Builder.Default
    private Integer keyImageIndex = -1; // 대표 이미지 인덱스

    @Column
    @Builder.Default
    private Long likes = 0L; //좋아요 수 (매번 집계 쿼리 사용을 피하기 위함)

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> urls; // 유저가 설정한 url list ex) github, npm 등등

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> imageUrls;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private Boolean isApproved; // 모음팀 측에서 인증됬는지 (타 앱 연동을 통해)

    @Column
    @Enumerated(EnumType.STRING)
    private ProjectCategory category; // 프로젝트 유형

    @Column
    @Convert(converter = ProjectStackListConverter.class)
    private List<ProjectStack> stacks;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    public Project update(String title, String bio, List<String> urls, Integer keyImageIndex, List<String> imageUrls, String content, ProjectCategory category, List<ProjectStack> stacks) {
        if (title != null) {
            this.title = title;
        }
        if (bio != null) {
            this.Bio = bio;
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
        if (category != null) {
            this.category = category;
        }
        if (stacks != null) {
            this.stacks = stacks;
        }
        return this;
    }
}

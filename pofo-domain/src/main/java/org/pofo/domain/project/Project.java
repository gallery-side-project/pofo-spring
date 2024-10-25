package org.pofo.domain.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pofo.domain.converter.StringListConverter;
import org.pofo.domain.user.User;

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

    @Column()
    private String Bio; // 한줄 소개

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> urls; // 유저가 설정한 url list ex) github, npm 등등

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> imageUrls;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column()
    private Boolean isApproved; // 모음팀 측에서 인증됬는지 (타 앱 연동을 통해)

    @Column()
    @Enumerated(EnumType.STRING)
    private ProjectCategory category; // 프로젝트 유형

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    public Project update(String title, String bio, List<String> urls, List<String> imageUrls, String content, ProjectCategory category) {
        if (title != null) {
            this.title = title;
        }
        if (bio != null) {
            this.Bio = bio;
        }
        if (urls != null) {
            this.urls = urls;
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
        return this;
    }
}

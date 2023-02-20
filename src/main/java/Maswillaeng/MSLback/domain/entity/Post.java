package Maswillaeng.MSLback.domain.entity;

import Maswillaeng.MSLback.dto.post.request.PostUpdateRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private String thumbnail;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(length = 100, nullable = false)
    private String content;
    private Long hits;
    private int report;

    @Builder
    public Post(User user, String thumbnail, String title, String content) {
        this.user = user;
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
        this.hits = 0L;
        this.report = 0;
    }

    public void update(PostUpdateRequestDto dto) {
        this.thumbnail = dto.getThumbnail();
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }
}

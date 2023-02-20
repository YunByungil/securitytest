package Maswillaeng.MSLback.dto.post.request;

import Maswillaeng.MSLback.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDto {

    private Long post_id;
    private String nickname;
    private String userImage;
    private String thumbnail;
    private String title;
    private String content;

    public PostDetailDto(Post post) {
        this.post_id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.userImage = post.getUser().getUserImage();
        this.thumbnail = post.getThumbnail();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}

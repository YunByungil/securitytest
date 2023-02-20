package Maswillaeng.MSLback.dto.post.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequestDto {

    private Long id;
    private String thumbnail;
    private String title;
    private String content;
}

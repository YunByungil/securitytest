package Maswillaeng.MSLback.dto.user.request;

import Maswillaeng.MSLback.domain.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserListDTO {
    private String email;
    private String nickname;
    private String phoneNumber;
    private String userImage;
    private String introduction;

    public UserListDTO(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.userImage = user.getUserImage();
        this.introduction = user.getIntroduction();
    }
}

package Maswillaeng.MSLback.domain.entity;

import Maswillaeng.MSLback.dto.user.request.UserUpdateDTO;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String nickname;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    private String userImage;
    private String introduction;
    @ColumnDefault("0")
    private int withdrawYn;
    @Enumerated(EnumType.STRING)
    private RoleType role;
    @Column(length = 3000)
    private String refresh_token;
    private LocalDateTime withdrawAt;

    @Builder
    public User(String email, String password, String nickname,
                String phoneNumber, String userImage, String introduction) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.userImage = userImage;
        this.introduction = introduction;
        this.withdrawYn = 0;
        this.role = RoleType.USER;
    }

    public void updateUser(UserUpdateDTO userUpdateDTO) {
        this.password = userUpdateDTO.getPassword();
        this.nickname = userUpdateDTO.getNickname();
        this.phoneNumber = userUpdateDTO.getPhoneNumber();
        this.userImage = userUpdateDTO.getUserImage();
        this.introduction = userUpdateDTO.getIntroduction();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }
}

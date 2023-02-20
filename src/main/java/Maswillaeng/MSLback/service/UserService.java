package Maswillaeng.MSLback.service;

import Maswillaeng.MSLback.domain.entity.RoleType;
import Maswillaeng.MSLback.domain.entity.User;
import Maswillaeng.MSLback.domain.repository.UserRepository;
import Maswillaeng.MSLback.dto.user.reponse.TokenResponse;
import Maswillaeng.MSLback.dto.user.reponse.UserLoginResponseDto;
import Maswillaeng.MSLback.dto.user.request.UserJoinDTO;
import Maswillaeng.MSLback.dto.user.request.UserLoginRequestDto;
import Maswillaeng.MSLback.utils.CookieUtil;
import Maswillaeng.MSLback.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Maswillaeng.MSLback.utils.JwtUtil.createJwt;
import static Maswillaeng.MSLback.utils.JwtUtil.getUserId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ValidateService validateService;
    private final CookieUtil cookieUtil;

    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String secretKey; // 시크릿 키

    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        /* 유저 존재 여부 확인 */
        User user = validateService.validateExistUser(dto);

        /* 비밀번호 맞는지 확인 */
        if(!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalStateException("비밀번호 틀림");
        }

        String accessToken = createJwt(user.getId(), user.getRole(), secretKey);
        String refreshToken = JwtUtil.createRefreshJwt(user.getId(), secretKey);
        user.updateRefreshToken(refreshToken);
        System.out.println("refreshToken = " + refreshToken);
//        userRepository.save(user);
        TokenResponse token = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return UserLoginResponseDto.builder()
                .tokenResponse(token)
                .nickname(user.getNickname())
                .userImage(user.getUserImage())
                .build();
    }

    @Transactional
    public Long join(UserJoinDTO dto) {
//        validateDuplicateEmail(user.getEmail());
        User user = userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return user.getId();
    }

//    @Transactional
//    public void update(Long userId, UserUpdateDTO userUpdateDTO) {
//        User user = userRepository.findOne(userId);
//        user.updateUser(userUpdateDTO);
//    }

    public User findOne(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("없는 회원"));
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

//    public void validateDuplicateEmail(String email) {
//        List<User> userEmail = userRepository.findByEmail(email);
//        if (!userEmail.isEmpty()) {
//            throw new IllegalStateException("이미 존재하는 Email입니다.");
//        }
//    }

    public TokenResponse reissueAccessToken(String refreshToken) {
        String token = "";
        System.out.println("엑세스토큰 재발급 완료 메서드");
        Long userId = getUserId(refreshToken, secretKey);
        System.out.println("userId = " + userId);
        User user = findOne(userId);
        if (user.getRefresh_token().equals(refreshToken)) {
            token = createJwt(userId, user.getRole(), secretKey);
            System.out.println("token = " + token);
        } else {
            new Exception("이상한 토큰을 넣었음!");
        }
        return TokenResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
//        return ResponseEntity.ok()
//                .header("Set-Cookie", token)
//                .header("Set-Cookie", refreshToken)
//                .body("");

    }
}

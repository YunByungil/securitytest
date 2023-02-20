package Maswillaeng.MSLback.utils;

import Maswillaeng.MSLback.dto.user.reponse.UserLoginResponseDto;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieUtil {

    public ResponseCookie createAccessCookieToken(UserLoginResponseDto userLoginResponseDto) {
        return ResponseCookie
                .from("accessToken", userLoginResponseDto.getTokenResponse().getAccessToken())
                .path("/")
                .httpOnly(true)
                // 시간
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRE_TIME)
                .sameSite("Lax")
                .build();
    }
    public ResponseCookie createCookie(String accessToken) {
        return ResponseCookie
                .from("accessToken", accessToken)
                .path("/")
                .httpOnly(true)
                // 시간
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRE_TIME)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie createRefreshCookieToken(UserLoginResponseDto userLoginResponseDto) {
        return ResponseCookie
                .from("refreshToken", userLoginResponseDto.getTokenResponse().getRefreshToken())
                .path("/")
                .httpOnly(true)
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRE_TIME)
                .sameSite("Lax")
                .build();
    }
}

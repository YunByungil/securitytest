package Maswillaeng.MSLback.utils;

import Maswillaeng.MSLback.domain.entity.RoleType;
import Maswillaeng.MSLback.domain.entity.User;
import Maswillaeng.MSLback.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
public class JwtUtil {
    private final UserService userService;
    private final CookieUtil cookieUtil;
    @Value("${jwt.secret}")
    private final String secretKey; // 시크릿 키
    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 100 * 60L; // AccessToken 시간 1분
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 120L; // RefreshToken 시간

    public static String createJwt(Long userId, RoleType roleType, String secretKey) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("role", roleType);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static String createRefreshJwt(Long userId, String secretKey) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static void reissueAccessToken(String refreshToken, String accessToken, String secretKey) {
        System.out.println("엑세스토큰 재발급 완료 메서드");
        System.out.println("secretKey = " + secretKey);
        Long userId = getUserId(refreshToken, secretKey);
        System.out.println("userId = " + userId);

        String token = createJwt(userId, RoleType.USER, secretKey);
        System.out.println("token = " + token);
//        cookieUtil.createAccessCookieToken()
    }


    /**
     * 유효 기간을 체크하는 메서드 Access
     * @param token Bearer를 제외한 토큰 정보
     * @param secretKey
     */
    public static boolean isExpired(String token, String secretKey) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                    .getBody().getExpiration().before(new Date());
            return true;
        } catch (ExpiredJwtException e) {

            return false;
        }

    }

    /**
     * 로그인 시, 회원 id를 알 수 있는 메서드
     * @param token 토큰 정보
     * @param secretKey
     */
    public static Long getUserId(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("userId", Long.class);
    }
}

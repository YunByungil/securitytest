package Maswillaeng.MSLback.config;

import Maswillaeng.MSLback.common.exception.ErrorCode;
import Maswillaeng.MSLback.service.UserService;
import Maswillaeng.MSLback.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static Maswillaeng.MSLback.common.exception.ErrorCode.*;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Value("${jwt.secret}")
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
        TODO: PrincipalDetails 구현
        TODO: 비회원일 때 처리
        TODO: 어떻게 하면 액세스 토큰이 만료 되었을 때 필터를 거치지 않고 발급 받을 수 있을까?
         */
        log.info("=== doFilterInternal ===");
        log.info("SecretKey : {}", secretKey);

        /* 쿠키에서 토큰 꺼내기 */

        Cookie[] cookies = request.getCookies();

        log.info("SecretKey2 : {}", secretKey);
//        if (cookies == null) {
//            log.error("토큰이 존재하지 않습니다.");
//            filterChain.doFilter(request, response);
//            return;
//        }
        if (ObjectUtils.isEmpty(cookies)) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("SecretKey3 : {}", secretKey);
        String accessCookie = "";
        String refreshCookie = "";
        for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                accessCookie = cookie.getValue();
            } else if (cookie.getName().equals("refreshToken")) {
                refreshCookie = cookie.getValue();
            }
        }

        log.info("SecretKey4 : {}", secretKey);
//        if (!(refreshCookie == null || refreshCookie.equals(""))) { // 리프레쉬 토큰이 존재하면
//            log.info("리프레시 토큰 존재한다");
//            log.info("refreshCookie = {}", refreshCookie);
//            exceptionRefresh(request, response, filterChain, refreshCookie);
//        }
//        else if (!(accessCookie == null || accessCookie.equals(""))) {
//            log.info("엑세스 토큰 존재한다");
//            exceptionAccess(request, response, filterChain, accessCookie);
//        }

        if (JwtUtil.isExpired(refreshCookie, secretKey)) { // 유효한 리프레시 토큰
            log.info("Filter: 리프레시 토큰 유효하다");
            if (JwtUtil.isExpired(accessCookie, secretKey)) { // 유효한 엑세스 토큰
                log.info("Filter: 엑세스 토큰 유효하다");
                exceptionAccess(request, response, filterChain, accessCookie);
            } else {
                log.error("Filter: 엑세스 토큰 만료되었음");
                exceptionAccess(request, response, filterChain, accessCookie);
//                userService.reissueAccessToken(refreshCookie, accessCookie, secretKey);
            }
        } else {
            log.error("Filter: 리프레시 토큰 만료되었음");
            exceptionRefresh(request, response, filterChain, refreshCookie);
        }
        log.info("SecretKey5 : {}", secretKey);

    }


    private void exceptionRefresh(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String refreshCookie) throws ServletException, IOException {
        // TODO: 아직 수정해야 됨 PrinciplaDetails를 구현해야 된다. (권한, login)
        try {
//            log.info("check = {}", JwtUtil.isExpired(refreshCookie, secretKey));
            Long userId = JwtUtil.getUserId(refreshCookie, secretKey);
//            System.out.println("userId111 = " + userId);
//            // 권한 부여
//            UsernamePasswordAuthenticationToken authentication
//                    = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
//            System.out.println("userId111 = " + userId);
//
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            System.out.println("userId111 = " + userId);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println("userId111 = " + userId);

        } catch (SecurityException | MalformedJwtException | ExpiredJwtException e) {
            log.info("리프레쉬 토큰 유효기간 만료되었음!!");
            request.setAttribute("exception", INVALID_TOKEN.name());
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            log.info("권한이 없는데용?");
            request.setAttribute("exception", INVALID_PERMISSION.name());
        } catch (Exception e) {
            log.info("알 수 없는 오류~!~!");
            request.setAttribute("exception", UNKNOWN_ERROR.name());
        } finally {
            filterChain.doFilter(request, response);
        }

    }

    private void exceptionAccess(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String accessCookie) throws ServletException, IOException {
        // TODO: 아직 수정해야 됨 PrinciplaDetails를 구현해야 된다. (권한, login)
        try {
            log.info("엑세스토큰 시작점");
            Long userId = JwtUtil.getUserId(accessCookie, secretKey);
            System.out.println("액세스토큰 = " + userId);
            // 권한 부여
            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            System.out.println("액세스토큰 = " + userId);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            System.out.println("액세스토큰 = " + userId);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("액세스토큰 = " + userId);

        } catch (SecurityException | MalformedJwtException | ExpiredJwtException e) {
            log.info("엑세스 토큰 유효기간 만료되었음!!");
            request.setAttribute("exception", INVALID_TOKEN.name());
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            log.info("권한이 없는데용?");
            request.setAttribute("exception", INVALID_PERMISSION.name());
        } catch (Exception e) {
            log.info("NullException");
            request.setAttribute("exception", UNKNOWN_ERROR.name());
        } finally {
            log.info("여길 지나네");
            filterChain.doFilter(request, response);
        }
    }


}

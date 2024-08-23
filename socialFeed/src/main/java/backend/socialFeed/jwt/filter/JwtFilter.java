package backend.socialFeed.jwt.filter;

import backend.socialFeed.jwt.util.CookieUtil;
import backend.socialFeed.jwt.util.JwtUtil;
import backend.socialFeed.jwt.service.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에서 acs token 추출
        String accessToken = cookieUtil.getAccessTokenFromCookie(request);
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            // acs token이 유효하면 사용자 정보 추출
            Claims claims = jwtUtil.getClaimsFromToken(accessToken);
            String email = claims.getSubject();

            // 인증 객체 생성
            setSecurityContext(email);

        } else if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            // rfr token이 유효한 경우 acs token을 재발급
            String email = jwtUtil.getEmailFromToken(refreshToken);

            // Redis에서 rfr token 확인
            String storedRefreshToken = redisService.getRefreshToken(email);
            if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
                // 새로운 acs token 생성
                String newAccessToken = jwtUtil.generateAccessToken(email);

                // 새 acs token을 쿠키에 저장
                cookieUtil.createAccessTokenCookie(response, newAccessToken, true);

                // 인증 객체 생성 및 설정
                setSecurityContext(email);
            } else {
                // rfr token이 Redis와 일치하지 않으면 로그아웃 처리 (쿠키 삭제)
                invalidateCookies(response);
            }
        } else {
            // 둘 다 유효하지 않으면 세션 및 쿠키 삭제 (로그아웃 처리)
            invalidateCookies(response);
        }

        filterChain.doFilter(request, response);
    }

    private void setSecurityContext(String email) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, Collections.singleton(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void invalidateCookies(HttpServletResponse response) {
        // Spring Security 컨텍스트 초기화
        SecurityContextHolder.clearContext();
        // 쿠키 삭제
        cookieUtil.deleteAccessTokenCookie(response, true);
        cookieUtil.deleteRefreshTokenCookie(response, true);
    }
}

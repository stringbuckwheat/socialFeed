package backend.socialFeed.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    // 쿠키 생성
    public void create(HttpServletResponse response, String name, String value, int expiry) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);  // HttpOnly 설정 (XSS 공격 방지)
        cookie.setMaxAge(expiry);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // acs token 쿠키 생성
    public void createAccessTokenCookie(HttpServletResponse response, String accessToken) {
        create(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, 15 * 60); // 15분 유효
    }

    // rfr token 쿠키 생성
    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        create(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, 7 * 24 * 60 * 60); // 7일 유효
    }

    // 쿠키에서 acs token 추출
    public String getAccessTokenFromCookie(HttpServletRequest request) {
        return getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    // 쿠키에서 rfr token 추출
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    // 쿠키 값 추출
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // acs token 쿠키 삭제
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        create(response, ACCESS_TOKEN_COOKIE_NAME, null, 0);
    }

    // rfr token 쿠키 삭제
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        create(response, REFRESH_TOKEN_COOKIE_NAME, null, 0);
    }
}
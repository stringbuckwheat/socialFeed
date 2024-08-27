package backend.socialFeed.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    private final Key key;

    public JwtUtil(@Value("${jwt.key}") String JWT_KEY) {
        this.key = new SecretKeySpec(JWT_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    // acs token 생성
    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    // rfr token 생성
    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    // 토큰 생성
    private String generateToken(String email, long expirationTime) {
        return Jwts
                .builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // 토큰 클레임 검사
            Claims claims = Jwts
                            .parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

            // 만료 시간 확인
            return !claims.getExpiration().before(Date.from(Instant.now()));
        } catch (Exception e) {
            // 예외 발생 시, 유효하지 않은 토큰으로 간주
            return false;
        }
    }

    // 토큰에서 Claims 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 사용자 이름 추출
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public List<String> getRolesFromToken(String newAccessToken) {
        try {
            // JWT 비밀 키를 사용하여 Claims 객체를 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(newAccessToken)
                    .getBody();

            List<String> roles = claims.get("roles", List.class);

            return roles != null ? roles : List.of(); // null 체크 후 반환
        } catch (SignatureException e) {
            // JWT 서명이 유효하지 않거나 토큰이 잘못된 경우 예외 처리
            throw new RuntimeException("유효하지 않은 토큰입니다.", e);
        } catch (Exception e) {
            // 다른 예외 처리
            throw new RuntimeException("토큰처리를 할 수 없습니다.", e);
        }
    }
}
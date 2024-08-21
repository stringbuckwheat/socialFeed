package backend.socialFeed.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.key}")
    private String JWT_KEY;
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    // Access Token 생성
    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    // 토큰 생성
    private String generateToken(String email, long expirationTime) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, JWT_KEY)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 Claims 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(JWT_KEY).parseClaimsJws(token).getBody();
    }

    // 토큰에서 사용자 이름 추출
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

}
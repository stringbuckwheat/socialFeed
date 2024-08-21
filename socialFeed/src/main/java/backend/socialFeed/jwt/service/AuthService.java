package backend.socialFeed.jwt.service;

import backend.socialFeed.jwt.util.CookieUtil;
import backend.socialFeed.jwt.util.JwtUtil;
import backend.socialFeed.user.constant.Constants;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, CookieUtil cookieUtil, RedisService redisService,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.redisService = redisService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void login(HttpServletResponse response, String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new IllegalArgumentException(Constants.EMAIL_NOT_EXISTS);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new IllegalArgumentException(Constants.PASSWORD_NOT_MATCH);
        }

        // acs token 및 rfr token 생성
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // rfr token을 Redis에 저장
        redisService.saveRefreshToken(email, refreshToken);

        // acs token 및 rfr token을 쿠키로 설정
        cookieUtil.createAccessTokenCookie(response, accessToken);
        cookieUtil.createRefreshTokenCookie(response, refreshToken);
    }


    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 acs token 및 rfr token 삭제
        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);

        // acs token에서 사용자 이메일 추출
        String accessToken = cookieUtil.getAccessTokenFromCookie(request);
        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            String email = jwtUtil.getEmailFromToken(accessToken);

            // Redis에서 rfr token 삭제
            redisService.deleteRefreshToken(email);
        }
    }
}
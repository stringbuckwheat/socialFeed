package backend.socialFeed.jwt.service;

import backend.socialFeed.jwt.util.CookieUtil;
import backend.socialFeed.jwt.util.JwtUtil;
import backend.socialFeed.user.constant.Constants;
import backend.socialFeed.user.dto.UserVerifyRequestDto;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<String> login(HttpServletResponse response, UserVerifyRequestDto verifyingUser) {
        // 유저 정보 존재 확인
        User user = userRepository
                                .findByEmail(verifyingUser.getEmail())
                                .orElseThrow(() -> new UsernameNotFoundException(Constants.EMAIL_NOT_EXISTS));

        // 비밀번호 검증
        if (!passwordEncoder.matches(verifyingUser.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(Constants.PASSWORD_NOT_MATCH);
        }

        // acs token 및 rfr token 생성
        String accessToken = jwtUtil.generateAccessToken(verifyingUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(verifyingUser.getEmail());

        // rfr token을 Redis에 저장
        redisService.saveRefreshToken(verifyingUser.getEmail(), refreshToken);

        // acs token 및 rfr token을 쿠키로 설정
        cookieUtil.createAccessTokenCookie(response, accessToken, true);
        cookieUtil.createRefreshTokenCookie(response, refreshToken, true);

        return ResponseEntity.ok(verifyingUser.getEmail());
    }


    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 acs token 및 rfr token 삭제
        cookieUtil.deleteAccessTokenCookie(response, true);
        cookieUtil.deleteRefreshTokenCookie(response, true);

        // acs token에서 사용자 이메일 추출
        String accessToken = cookieUtil.getAccessTokenFromCookie(request);
        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            String email = jwtUtil.getEmailFromToken(accessToken);

            // Redis에서 rfr token 삭제
            redisService.deleteRefreshToken(email);
        }

        return ResponseEntity.ok(Constants.LOG_OUT);
    }
}
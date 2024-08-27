package backend.socialFeed.jwt.controller;

import backend.socialFeed.auth.config.SecurityConfig;
import backend.socialFeed.jwt.repository.RefreshTokenRepository;
import backend.socialFeed.jwt.service.AuthService;
import backend.socialFeed.jwt.service.RedisService;
import backend.socialFeed.jwt.util.CookieUtil;
import backend.socialFeed.jwt.util.JwtUtil;
import backend.socialFeed.user.dto.UserVerifyRequestDto;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, CookieUtil.class, JwtUtil.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RedisService redisService;

//    @MockBean
//    JwtUtil jwtUtil;

    public void perform(UserVerifyRequestDto userDto, int code) throws Exception {
        // 사용자 정보 설정
        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .build();

        // Mockito를 이용한 동작 설정
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // JSON 데이터 변환
        String content = objectMapper.writeValueAsString(userDto);

        // MockMvc를 이용해 요청 수행 및 응답 검증
        mvc.perform(post("/auth/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(code));
    }

    /**
     * 로그인 테스트
     */
    @Test
    public void testLogin() throws Exception {
        // 테스트 유저 설정
        UserVerifyRequestDto userDto = UserVerifyRequestDto.builder()
                .email("test@test.com")
                .password("password")
                .build();

        // 정상 로그인 테스트
        perform(userDto, 200);
    }

    /**
     * 로그아웃 테스트
     */
    @Test
    public void testLogout() throws Exception {
        // 로그아웃 처리 설정
        when(authService.logout(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(ResponseEntity.ok("로그아웃 완료"));

        // 로그아웃 요청 및 응답 검증
        mvc.perform(post("/auth/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("로그아웃 완료"));
    }
}

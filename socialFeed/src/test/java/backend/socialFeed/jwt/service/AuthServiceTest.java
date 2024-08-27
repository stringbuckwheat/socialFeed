package backend.socialFeed.jwt.service;

import backend.socialFeed.jwt.util.CookieUtil;
import backend.socialFeed.jwt.util.JwtUtil;
import backend.socialFeed.user.constant.Constants;
import backend.socialFeed.user.dto.UserVerifyRequestDto;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    private RedisService redisService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    /**
     * 로그인 테스트
     */
    @Test
    public void testLogin() {
        // given
        UserVerifyRequestDto userDto = UserVerifyRequestDto.builder()
                                                            .email("test@test.com")
                                                            .password("password")
                                                            .build();
        User user = User.builder()
                        .email(userDto.getEmail())
                        .password(passwordEncoder.encode(userDto.getPassword()))
                        .build();

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateAccessToken(any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh-token");

        // when
        ResponseEntity<String> result = authService.login(response, userDto);

        // then
        assertThat(result.getBody()).isEqualTo(userDto.getEmail());
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
    }

    /**
     * 로그아웃 테스트
     */
    @Test
    public void testLogout() {
        // given
        String accessToken = "valid-access-token";
        cookieUtil.createAccessTokenCookie(response, accessToken, true);

        when(cookieUtil.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtUtil.validateToken(accessToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(accessToken)).thenReturn("test@test.com");

        // when
        ResponseEntity<String> result = authService.logout(request, response);

        // then
        assertThat(result.getBody()).isEqualTo(Constants.LOG_OUT);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
    }
}

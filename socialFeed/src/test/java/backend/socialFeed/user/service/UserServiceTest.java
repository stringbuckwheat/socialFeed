package backend.socialFeed.user.service;

import backend.socialFeed.user.constant.Constants;
import backend.socialFeed.user.dto.JoinValidRequestDto;
import backend.socialFeed.user.dto.UserVerifyRequestDto;
import backend.socialFeed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    JoinValidRequestDto joinValidRequestDto;

    @BeforeEach
    void setUp() {
        joinValidRequestDto =
                JoinValidRequestDto.builder().email("validemail@gmail.com").password("encodedPassword").build();
        userService.join(joinValidRequestDto);
    }

    private void assertUserVerification(String email, String password, String code, String expectedMessage) {
        UserVerifyRequestDto requestDto = UserVerifyRequestDto.builder()
                .email(email)
                .password(password)
                .code(code)
                .build();
        assertThrows(IllegalStateException.class, () -> userService.verifyUser(requestDto), expectedMessage);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 가입 시도시 에러")
    void userJoinThrowsEmailAlreadyExists() {
        JoinValidRequestDto requestDto = JoinValidRequestDto.builder()
                .email("validemail@gmail.com")
                .password("testPassword01!")
                .build();

        assertThrows(IllegalStateException.class, () -> userService.join(requestDto), Constants.EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("존재하지 않는 이메일")
    void verifyUserThrowsEmailNotExists() {
        String code = userRepository.findByEmail(joinValidRequestDto.getEmail()).get().getCode();
        assertUserVerification("nonexistemail@gmail.com", joinValidRequestDto.getPassword(), code, Constants.EMAIL_NOT_EXISTS);
    }

    @Test
    @DisplayName("올바르지 않은 비밀번호")
    void verifyUserThrowsPasswordNotMatch() {
        String code = userRepository.findByEmail(joinValidRequestDto.getEmail()).get().getCode();
        assertUserVerification(joinValidRequestDto.getEmail(), "nonmatchpassword", code, Constants.PASSWORD_NOT_MATCH);
    }

    @Test
    @DisplayName("발급된 6자리 코드가 아닐 때")
    void verifyUserThrowsCodeNotMatch() {
        assertUserVerification(joinValidRequestDto.getEmail(), joinValidRequestDto.getPassword(), "NOCODE", Constants.CODE_NOT_MATCH);
    }

    @Test
    @DisplayName("verify 가 잘 승인되었을때")
    void verifyUserSuccess() {
        boolean beforeVerified = userRepository.findByEmail(joinValidRequestDto.getEmail()).get().isVerified();
        assertFalse(beforeVerified);

        UserVerifyRequestDto requestDto =
                UserVerifyRequestDto.builder().email(joinValidRequestDto.getEmail()).password(joinValidRequestDto.getPassword())
                        .code(userRepository.findByEmail(joinValidRequestDto.getEmail()).get().getCode()).build();
        userService.verifyUser(requestDto);

        boolean afterVerified = userRepository.findByEmail(joinValidRequestDto.getEmail()).get().isVerified();
        assertTrue(afterVerified);
    }
}


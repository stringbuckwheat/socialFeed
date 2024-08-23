package backend.socialFeed.user.controller;

import backend.socialFeed.auth.config.SecurityConfig;
import backend.socialFeed.user.dto.JoinValidRequestDto;
import backend.socialFeed.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    private JoinValidRequestDto baseRequestDto;

    @BeforeEach
    void setup() {
        baseRequestDto = JoinValidRequestDto.builder()
                .email("validemail@gmail.com")
                .password("socialFeed1357!")
                .build();
    }

    private void performRequest(JoinValidRequestDto requestDto, int expectedStatus) throws Exception {
        String content = objectMapper.writeValueAsString(requestDto);

        mvc.perform(post("/user/register")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    @DisplayName("올바른 이메일 형식이 아닐때")
    void userJoinThrowsNonValidEmailError() throws Exception {
        baseRequestDto.setEmail("NONVALIDEMAIL");
        performRequest(baseRequestDto, 400); // 400: Bad Request
    }

    @DisplayName("회원가입 성공")
    @Test
    void userJoinSuccess() throws Exception {
        performRequest(baseRequestDto, 201); // 201: Created
    }

    @Test
    @DisplayName("비밀번호는 10자 이상이어야 하고, 통상적으로 자주 사용되는 비밀번호(ex.123456) 는 사용할 수 없습니다.")
    void nonValidPassword() throws Exception {
        baseRequestDto.setPassword("123456");
        performRequest(baseRequestDto, 400); // 400: Bad Request
    }

    @Test
    @DisplayName("비밀번호에는 3회 이상 연속된 문자가 포함될 수 없습니다.")
    void nonValidPassword2() throws Exception {
        baseRequestDto.setPassword("aaabbbcccddd");
        performRequest(baseRequestDto, 400); // 400: Bad Request
    }

    @Test
    @DisplayName("비밀번호는 숫자, 문자, 특수문자 중 2가지 이상을 포함해야 합니다.")
    void nonValidPassword3() throws Exception {
        baseRequestDto.setPassword("passwordPWPW");
        performRequest(baseRequestDto, 400); // 400: Bad Request
    }

}

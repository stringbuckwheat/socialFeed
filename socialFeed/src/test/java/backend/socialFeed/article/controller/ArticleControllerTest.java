package backend.socialFeed.article.controller;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.dto.ArticleResponseDto;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.service.ArticleService;
import backend.socialFeed.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ArticleController articleController;

    private MockMvc mockMvc;
    private ArticleResponseDto response;
    private Article article;
    private User user;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(articleController)
                .setValidator(mock(Validator.class))
                .build();

        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .code("ebadf")
                .verified(true)
                .password("test")
                .build();

        response = ArticleResponseDto.builder()
                .id("article1")
                .userId(user.getId())
                .type(ArticleType.INSTAGRAM)
                .title("title1")
                .content("content1")
                .likeCount(0)
                .shareCount(0)
                .viewCount(0)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .updatedAt(LocalDateTime.of(2024, 1, 31, 1, 1, 1))
                .build();


        article = Article.builder()
                .id("article1")
                .user(user)
                .type(ArticleType.INSTAGRAM)
                .title("title1")
                .content("content1")
                .likeCount(0)
                .shareCount(0)
                .viewCount(0)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .updatedAt(LocalDateTime.of(2024, 1, 31, 1, 1, 1))
                .build();
    }

    @Nested
    class Read {
        @Test
        void success() throws Exception {
            // given
            doReturn(article).when(articleService).getArticle(any(String.class));

            // when
            ResultActions resultActions = mockMvc.perform(get("/api/article/" + article.getId())
            );

            // then
            resultActions.andExpectAll(
                    status().isOk(),
                    jsonPath("$.title").value(response.getTitle()),
                    jsonPath("$.viewCount").value(response.getViewCount()),
                    jsonPath("$.type").value(response.getType().toString())
            );
        }
    }
}

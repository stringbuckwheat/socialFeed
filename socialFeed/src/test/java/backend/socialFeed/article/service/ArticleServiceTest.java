package backend.socialFeed.article.service;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.repository.ArticleRepository;
import backend.socialFeed.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    private User user;
    private Article article;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .code("ebadf")
                .email("test@test.com")
                .password("test1")
                .verified(true)
                .build();

        article = Article.builder()
                .id("article1")
                .user(user)
                .type(ArticleType.FACEBOOK)
                .title("title1")
                .content("content1")
                .viewCount(0)  // 초기 viewCount 0
                .shareCount(0)
                .likeCount(0)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .updatedAt(LocalDateTime.of(2024, 1, 31, 1, 1, 1))
                .build();
    }

    @Nested
    class Read {
        @Test
        void success() {
            // given
            // articleRepository.findById() 호출 시 article 반환
            doReturn(Optional.of(article)).when(articleRepository).findById(article.getId());
            doReturn(article).when(articleRepository).save(article);

            // when
            // articleRepository.save()가 호출되면 저장된 article을 반환
            Article found = articleService.getArticle(article.getId());

            // then
            // 게시글 조회 기능 검증
            assertThat(found.getId()).isEqualTo(article.getId());
            assertThat(found.getUser().getId()).isEqualTo(user.getId());
            assertThat(found.getTitle()).isEqualTo(article.getTitle());

            // 게시글 조회 시 viewCount 가 1 증가되는 로직 검증
            assertThat(found.getViewCount()).isEqualTo(1);
        }

        @Test
        void failNotFoundArticle() {
            // given
            // articleRepository.findById() 호출 시 빈 값 반환
            doReturn(Optional.empty()).when(articleRepository).findById(article.getId());

            // when
            // ArticleRepository 에 article 을 찾지 못하므로 예외 발생
            var thrown = assertThrows(IllegalArgumentException.class, () -> articleService.getArticle(article.getId()));

            // then
            // 에러 클래스와 에러 메시지 검증
            assertThat(thrown.getClass()).isEqualTo(IllegalArgumentException.class);
            assertThat(thrown.getMessage()).isEqualTo("Article with id " + article.getId() + " not found.");
        }
    }
}

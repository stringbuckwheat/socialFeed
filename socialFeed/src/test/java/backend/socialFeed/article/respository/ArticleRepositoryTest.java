package backend.socialFeed.article.respository;

import backend.socialFeed.article.ArticleType;
import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.repository.ArticleRepository;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;

    private Article article;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .code("abcdef")
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
                .viewCount(0)
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
            User savedUser = userRepository.save(user);
            Article savedArticle = articleRepository.save(article);

            // when
            Optional<Article> found = articleRepository.findById(savedArticle.getId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getUser()).isEqualTo(savedUser);
            assertThat(found.get().getId()).isEqualTo(article.getId());
            assertThat(found.get().getTitle()).isEqualTo(article.getTitle());
        }

        @Test
        void fail() {
            // given
            // when
            Optional<Article> found = articleRepository.findById("");

            // then
            assertThat(found).isNotPresent();
        }
    }

}

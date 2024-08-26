package backend.socialFeed.statistic.repository;

import backend.socialFeed.article.entity.Article;
import backend.socialFeed.article.repository.ArticleRepository;
import backend.socialFeed.common.config.QueryDslConfig;
import backend.socialFeed.hashtags.entity.Hashtags;
import backend.socialFeed.statistic.dto.StatisticRequest;
import backend.socialFeed.statistic.dto.StatisticResponse;
import backend.socialFeed.user.model.User;
import backend.socialFeed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class, ArticleQueryRepository.class})
@DisplayName("통계 Repository 테스트")
class ArticleQueryRepositoryTest {
    @Autowired
    private ArticleQueryRepository articleQueryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    public void setUp() {
        // 기준일
        User user = userRepository.save(User.builder().email("email").verified(true).build());

        for (int i = 0; i < 10; i++) {
            Hashtags hashtag = Hashtags.builder().name("stringbuckwheat test").build();

            Article a = Article.builder()
                    .id(UUID.randomUUID().toString())
                    .content("content " + i)
                    .title("HOUR article " + i)
                    .likeCount(i)
                    .shareCount(i * 2)
                    .viewCount(i * 3)
                    .hashtags(new ArrayList<>())
                    .user(user)
                    .build();

            a.addHashtag(hashtag);

            articleRepository.save(a);
        }
    }


    @ParameterizedTest
    @DisplayName("Type, Value 경우의 수에 따른 테스트 시나리오 테스트")
    @ArgumentsSource(StatisticRequestProvider.class)
    public void getStatistics(StatisticRequest request, int listSize, int count) {
        List<StatisticResponse> result = articleQueryRepository.getStatistics(request);

        assertEquals(listSize, result.size());
        assertEquals(count, result.get(0).getCount());
    }
}
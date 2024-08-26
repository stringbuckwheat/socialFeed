package backend.socialFeed.statistic.repository;

import backend.socialFeed.statistic.dto.StatisticRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StatisticRequestProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        int likeCount = 0;

        for (int i = 0; i < 10; i++) {
            likeCount += i;
        }

        int shareCount = 2 * likeCount;
        int viewCount = 3 * likeCount;

        String hashtag = "stringbuckwheat test";

        LocalDateTime start = LocalDateTime.now().minusDays(1);

        return Stream.of(
                // 1. HOUR / COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "hour",
                                start,
                                start.plusDays(2),
                                "count",
                                "testUsername"),
                        1,
                        10
                ),
                // 2. HOUR / VIEW_COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "hour",
                                start,
                                start.plusDays(2),
                                "view_count",
                                "testUsername"),
                        1,
                        viewCount
                ),

                // 3. HOUR / LIKE COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "hour",
                                start,
                                start.plusDays(2),
                                "like_count",
                                "testUsername"),
                        1,
                        likeCount
                ),

                // 4. HOUR / SHARE_COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "hour",
                                start,
                                start.plusDays(2),
                                "share_count",
                                "testUsername"),
                        1,
                        shareCount
                ),

                //////////////////////// DATE
                // 5. DATE / COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "date",
                                start,
                                start.plusDays(2),
                                "count",
                                "testUsername"),
                        1,
                        10
                ),
                // 6. DATE / VIEW_COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "date",
                                start,
                                start.plusDays(2),
                                "view_count",
                                "testUsername"),
                        1,
                        viewCount
                ),

                // 7. DATE / LIKE_COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "date",
                                start,
                                start.plusDays(2),
                                "like_count",
                                "testUsername"),
                        1,
                        likeCount
                ),

                // 8. DATE / SHARE_COUNT
                Arguments.of(
                        StatisticRequest.createWithDefaults(
                                hashtag,
                                "date",
                                start,
                                start.plusDays(2),
                                "share_count",
                                "testUsername"),
                        1,
                        shareCount
                )
        );
    }
}

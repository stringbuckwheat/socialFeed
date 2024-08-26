package backend.socialFeed.statistic.service;

import backend.socialFeed.statistic.dto.StatisticRequest;
import backend.socialFeed.statistic.dto.StatisticResponse;
import backend.socialFeed.statistic.repository.ArticleQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("통계 컨트롤러 테스트")
public class StatisticServiceImplTest {

    @Mock
    private ArticleQueryRepository articleQueryRepository;

    @InjectMocks
    private StatisticServiceImpl statisticService;

    @Test
    @DisplayName("일별 조회 시, 통계 결과가 없는 날 count 0")
    public void getStatistic_WhenDate_ShouldReturnStatisticResponseWithDefaultZero() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 8, 21, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 23, 0, 0);
        StatisticRequest request = StatisticRequest.createWithDefaults(
                "hashtag", "date", start, end, "count", "username"
        );

        List<StatisticResponse> mockResponses = Arrays.asList(
                new StatisticResponse("2024-08-21", 5),
                new StatisticResponse("2024-08-22", 10)
        );

        when(articleQueryRepository.getStatistics(request)).thenReturn(mockResponses);

        // When
        List<StatisticResponse> result = statisticService.getStatistics(request);

        // Then
        // 통계 결과가 없는 날짜에 0이 들어가는지 확인
        List<StatisticResponse> expected = Arrays.asList(
                new StatisticResponse("2024-08-21", 5),
                new StatisticResponse("2024-08-22", 10),
                new StatisticResponse("2024-08-23", 0)
        );

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("시간대별 조회 시, 통계 결과가 없는 시간 count 0")
    public void getStatistic_WhenHour_ShouldReturnStatisticResponseWithDefaultZero() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 8, 21, 18, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 21, 20, 0);
        StatisticRequest request = StatisticRequest.createWithDefaults(
                "hashtag", "hour", start, end, "count", "username"
        );

        List<StatisticResponse> mockResponses = Arrays.asList(
                new StatisticResponse("2024-08-21 18:00", 3),
                new StatisticResponse("2024-08-21 19:00", 7)
        );

        when(articleQueryRepository.getStatistics(request)).thenReturn(mockResponses);

        // When
        List<StatisticResponse> result = statisticService.getStatistics(request);

        // Then
        // 통계 결과가 없는 날짜/시간에 0이 들어가는지 확인
        List<StatisticResponse> expected = Arrays.asList(
                new StatisticResponse("2024-08-21 18:00", 3),
                new StatisticResponse("2024-08-21 19:00", 7),
                new StatisticResponse("2024-08-21 20:00", 0)
        );

        assertEquals(expected, result);
    }
}

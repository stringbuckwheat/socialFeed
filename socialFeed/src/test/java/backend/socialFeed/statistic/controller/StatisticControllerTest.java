package backend.socialFeed.statistic.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import backend.socialFeed.statistic.dto.StatisticRequest;
import backend.socialFeed.statistic.dto.StatisticResponse;
import backend.socialFeed.statistic.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticController.class)
public class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticService statisticService;

    private List<StatisticResponse> mockResponses;

    @BeforeEach
    public void setUp() {
        mockResponses = Arrays.asList(
                new StatisticResponse("2024-08-21", 5),
                new StatisticResponse("2024-08-22", 10)
        );
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("통계: 정상 동작 시나리오 - 모든 파라미터 다 들어온 경우")
    public void getStatistic_WhenValid_ShouldReturnStatisticResponses() throws Exception {
        // Given
        Mockito.when(statisticService.getStatistics(any(StatisticRequest.class))).thenReturn(mockResponses);

        // When & Then
        mockMvc.perform(get("/statistics")
                        .param("type", "date")
                        .param("start", "2024-08-21T00:00:00")
                        .param("end", "2024-08-23T00:00:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-08-21"))
                .andExpect(jsonPath("$[0].count").value(5))
                .andExpect(jsonPath("$[1].date").value("2024-08-22"))
                .andExpect(jsonPath("$[1].count").value(10));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("통계: 정상 동작 시나리오 - default value 로직 검증")
    public void getStatistic_WhenDefaultValueLoginWorking_ShouldReturnStatisticResponses() throws Exception {
        // Given
        Mockito.when(statisticService.getStatistics(any(StatisticRequest.class)))
                .thenReturn(mockResponses);

        // When & Then
        mockMvc.perform(get("/statistics")
                        // Required 값 제외하고 모두 null으로 요청
                        .param("type", "date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-08-21"))
                .andExpect(jsonPath("$[0].count").value(5))
                .andExpect(jsonPath("$[1].date").value("2024-08-22"))
                .andExpect(jsonPath("$[1].count").value(10));
    }

    static Stream<Arguments> provideInvalidParameter() {
        return Stream.of(
                Arguments.of("type", "datee"), // type이 date, hour 중 하나가 아님
                Arguments.of("value", "countt") // value가 count, like_count, view_count, share_count 중 하나가 아님
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParameter")
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("통계: 유효하지 않은 파라미터 - type, value invalid")
    public void getStatistic_WhenParameterIsInvalid_ShouldThrowIllegalArgumentExceptionWithBadRequest(String name, String value) throws Exception {
        // When & Then
        mockMvc.perform(get("/statistics")
                        .param(name, value)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> provideInvalidDateRange() {
        return Stream.of(
                // start가 end보다 미래
                Arguments.of("date", "2024-08-23T00:00:00", "2024-08-21T00:00:00"),
                // 일별 통계인데 30일 초과
                Arguments.of("date", "2024-08-01T00:00:00", "2024-08-31T00:00:00"),
                // 시간별 통계인데 7일 초과
                Arguments.of("hour", "2024-08-01T00:00:00", "2024-08-08T00:00:00")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDateRange")
    @WithMockUser(username = "testuser@example.com")
    @DisplayName("날짜 유효성 검사 실패")
    public void getStatistic_WhenDateRangeIsInvalid_ShouldThrowIllegalArgumentExceptionWithBadRequest(String type, String start, String end) throws Exception {
        // When & Then
        mockMvc.perform(get("/statistics")
                        .param("type", type)
                        .param("start", start)
                        .param("end", end)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

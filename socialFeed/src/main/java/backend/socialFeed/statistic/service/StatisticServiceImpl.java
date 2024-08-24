package backend.socialFeed.statistic.service;

import backend.socialFeed.statistic.constant.StatisticType;
import backend.socialFeed.statistic.dto.StatisticRequest;
import backend.socialFeed.statistic.dto.StatisticResponse;
import backend.socialFeed.statistic.repository.ArticleQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final ArticleQueryRepository articleQueryRepository;

    /**
     * 게시글 통계 조회
     *
     * @param statisticRequest 통계 요청 정보
     * @return 통계 결과 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<StatisticResponse> getStatistics(StatisticRequest statisticRequest) {
        // 통계 결과가 존재하는 날짜/시간의 데이터를 가져온 뒤, Map으로 변환
        Map<String, Integer> resultMap = articleQueryRepository.getStatistics(statisticRequest).stream()
                .collect(Collectors.toMap(
                        StatisticResponse::getDate, // 날짜
                        StatisticResponse::getCount // 통계 값
                ));

        // 주어진 기간 동안의 LocalDateTime 리스트 생성
        List<String> dateList = createDateList(statisticRequest);

        // 3. 모든 날짜 또는 시간에 대해 결과를 생성
        // 데이터가 존재하지 않는 날짜/시간은 count 0
        List<StatisticResponse> finalResults = dateList.stream()
                .map(dateTime -> new StatisticResponse(
                        dateTime,
                        resultMap.getOrDefault(dateTime, 0) // 데이터가 없으면 0으로 설정
                ))
                .toList();

        return finalResults;
    }

    /**
     * 주어진 통계 요청에 따른 날짜/시간 리스트
     *
     * @param statisticRequest
     * @return 날짜/시간 리스트, 통계 타입 따라 문자열 형식으로 포맷
     *         DATE의 경우 "2024-08-23", "2024-08-24", "2024-08-25",
     *         HOUR의 경우 "2024-08-23 18:00", "2024-08-23 19:00", "2024-08-23 20:00"...
     */
    private List<String> createDateList(StatisticRequest statisticRequest) {
        List<String> dateList = new ArrayList<>();

        // 시작 날짜/시간 초기화
        ChronoUnit unit = (statisticRequest.getType() == StatisticType.DATE) ? ChronoUnit.DAYS : ChronoUnit.HOURS;
        LocalDateTime current = statisticRequest.getStart().truncatedTo(unit);

        // DATE/HOUR 타입에 따라 날짜 포맷팅
        String pattern = (statisticRequest.getType() == StatisticType.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // 종료 날짜/시간까지 순환하며 리스트에 추가
        while (!current.isAfter(statisticRequest.getEnd())) {
            dateList.add(current.format(formatter));
            current = current.plus(1, unit); // 1일 또는 1시간씩 증가
        }

        return dateList;
    }
}
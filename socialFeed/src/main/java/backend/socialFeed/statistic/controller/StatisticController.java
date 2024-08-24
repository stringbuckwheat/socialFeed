package backend.socialFeed.statistic.controller;

import backend.socialFeed.statistic.dto.StatisticRequest;
import backend.socialFeed.statistic.dto.StatisticResponse;
import backend.socialFeed.statistic.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
    private final StatisticService statisticService;

    /**
     * 게시글 통계 조회
     *
     * @param userDetails 인증된 사용자 정보. hashtag 미지정 시 기본값을 username(email)로 설정하기 위해
     * @param hashtag 필터링할 해시태그. 미지정 시 username(email)
     * @param type date(일별 조회), hour(시간대 별 조회) - 필수값!
     * @param start 통계 조회 시작 날짜/시간
     *              일별 조회도 2024-08-23T00:00:00 꼴의 요청 데이터 필요
     *              미지정 시 현 시각 기준 일주일 전
     * @param end 통계 조회의 종료 날짜/시간
     *            일별 조회도 2024-08-23T00:00:00 꼴의 요청 데이터 필요
     *            미지정 시 오늘
     * @param value count(게시글 수), view_count(조회 수), share_count(공유 수), like_count(좋아요 수)
     * @return 통계 데이터 리스트
     */
    @GetMapping("/statistics")
    public ResponseEntity<List<StatisticResponse>> getStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "hashtag", required = false) String hashtag,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "start", required = false) LocalDateTime start,
            @RequestParam(value = "end", required = false) LocalDateTime end,
            @RequestParam(value = "value", required = false, defaultValue = "count") String value
    ) {
        String username = userDetails.getUsername();
        StatisticRequest request = StatisticRequest.createWithDefaults(hashtag, type, start, end, value, username);

        return ResponseEntity.ok().body(statisticService.getStatistics(request));
    }
}
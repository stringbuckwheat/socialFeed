package backend.socialFeed.statistic.dto;

import backend.socialFeed.statistic.constant.StatisticType;
import backend.socialFeed.statistic.constant.StatisticValue;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static backend.socialFeed.statistic.constant.ErrorMessage.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticRequest {
    private String hashtag; // default 본인 계정

    private StatisticType type; // 필수값

    private LocalDateTime start; // 일주일 전

    private LocalDateTime end; // 오늘

    private StatisticValue value; // default count

    /**
     * 통계 요청 DTO 생성용 정적 메소드
     * 입력 파라미터에 따라 기본 값 설정 및 유효성 검사 수행
     *
     * @param hashtag 해시태그 (기본값: 사용자 이메일)
     * @param type 통계 타입(일별/월별), 필수
     * @param start 통계 시작 날짜 및 시간 (기본값: 오늘로부터 일주일 전)
     * @param end 통계 종료 날짜 및 시간 (기본값: 오늘)
     * @param value 통계 유형(게시글 수, 좋아요 수 등)
     * @param email 해시태그 null일 시 사용할 사용자 이메일
     * @return 유효성 검사를 통과한 StatisticRequest 객체
     *
     * @throws IllegalArgumentException 1) 시작 날짜가 종료 날짜보다 미래인 경우
     *                                  2) 통계 타입과 기간에 따라 쿼리가 너무 긴 경우
     *                                  3) 지원하지 않는 통계 타입/유형이 도착한 경우
     */
    public static StatisticRequest createWithDefaults(
            String hashtag,
            String type,
            LocalDateTime start,
            LocalDateTime end,
            String value,
            String email
    ) {
        // 기본값 설정
        String finalHashTag = (hashtag == null) ? email : hashtag;
        StatisticType finalType = StatisticType.from(type);
        LocalDateTime finalStart = (start == null) ? LocalDateTime.now().minusDays(7) : start;
        LocalDateTime finalEnd = (end == null) ? LocalDateTime.now() : end;
        StatisticValue finalValue = StatisticValue.from(value);

        // 통계 시작일, 종료일 유효성 검사
        long daysBetween = ChronoUnit.DAYS.between(finalStart, finalEnd);

        if(daysBetween < 0) {
            // 시작일이 종료일보다 미래
            throw new IllegalArgumentException(START_AFTER_END);
        } else if(finalType == StatisticType.DATE && daysBetween > 30) {
            // 일별 통계 시 30일 초과 불가
            throw new IllegalArgumentException(TOO_LONG_DATE_QUERY);
        } else if(finalType == StatisticType.HOUR && daysBetween > 7) {
            // 시간별 통계 시 7일 초과 불가
            throw new IllegalArgumentException(TOO_LONG_HOUR_QUERY);
        }

        // 유효한 `StatisticRequest` 객체를 생성하여 반환
        return new StatisticRequest(finalHashTag, finalType, finalStart, finalEnd, finalValue);
    }
}
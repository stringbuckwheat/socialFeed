package backend.socialFeed.statistic.constant;

public class ErrorMessage {
    public static final String START_AFTER_END = "시작 날짜(start)는 종료 날짜(end) 이후일 수 없습니다";
    public static final String TOO_LONG_DATE_QUERY = "일별 조회 기간은 30일을 초과할 수 없어요";
    public static final String TOO_LONG_HOUR_QUERY = "시간대 별 조회 기간은 7일을 초과할 수 없어요";
    public static final String INVALID_STATISTIC_TYPE = "검색 타입은 date, hour 중 하나여야 합니다.";
    public static final String INVALID_STATISTIC_VALUE = "검색 타입은 count, view_count, like_count, share_count 중 하나여야 합니다.";
}

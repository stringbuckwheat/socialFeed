package backend.socialFeed.statistic.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static backend.socialFeed.statistic.constant.ErrorMessage.INVALID_STATISTIC_VALUE;

/**
 * 통계 유형 Enum
 */
@AllArgsConstructor
@Getter
public enum StatisticValue {
    COUNT("count"),
    VIEW_COUNT("view_count"),
    LIKE_COUNT("like_count"),
    SHARE_COUNT("share_count");

    public final String value;

    /**
     * 문자열을 사용해 통계 유형 반환
     *
     * @param value "count", "view_count", "like_count", "share_count"만 유효
     * @return 해당 문자열 값에 맞는 StatisticValue
     * @throws IllegalArgumentException 유효하지 않은 문자열이 들어온 경우
     */
    public static StatisticValue from(String value) {
        return Arrays.stream(StatisticValue.values())
                .filter(statisticValue -> statisticValue.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(INVALID_STATISTIC_VALUE));
    }
}
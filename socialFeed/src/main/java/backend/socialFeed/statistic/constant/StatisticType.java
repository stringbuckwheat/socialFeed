package backend.socialFeed.statistic.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static backend.socialFeed.statistic.constant.ErrorMessage.INVALID_STATISTIC_TYPE;

/**
 * 통계 타입 Enum
 * DATE, HOUR 지원
 */
@Getter
@AllArgsConstructor
public enum StatisticType {
    DATE("date"),
    HOUR("hour");

    public final String type;

    /**
     * 문자열을 사용해 통계 타입 반환
     *
     * @param type "date", "hour"만 유효
     * @return 해당 문자열 값에 맞는 StatisticType
     * @throws IllegalArgumentException "date", "hour"가 아닌 문자열이 들어온 경우
     */
    public static StatisticType from(String type) {
        return Arrays.stream(StatisticType.values())
                .filter(statisticType -> statisticType.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(INVALID_STATISTIC_TYPE));
    }
}

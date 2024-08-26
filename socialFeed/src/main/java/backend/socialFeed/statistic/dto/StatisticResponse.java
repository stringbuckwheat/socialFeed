package backend.socialFeed.statistic.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
public class StatisticResponse {
    private String date; // 일별/시간대별 통계 결과를 위해 String으로 포맷
    private Integer count;

    @QueryProjection
    public StatisticResponse(String date, Integer count) {
        this.date = date;
        this.count = count;
    }
}

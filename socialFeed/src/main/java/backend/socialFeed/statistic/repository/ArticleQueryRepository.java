package backend.socialFeed.statistic.repository;


import backend.socialFeed.statistic.constant.StatisticType;
import backend.socialFeed.statistic.constant.StatisticValue;
import backend.socialFeed.statistic.dto.QStatisticResponse;
import backend.socialFeed.statistic.dto.StatisticRequest;
import backend.socialFeed.statistic.dto.StatisticResponse;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static backend.socialFeed.article.entity.QArticle.article;
import static backend.socialFeed.hashtags.entity.QHashtags.hashtags;
import static backend.socialFeed.statistic.constant.ErrorMessage.INVALID_STATISTIC_VALUE;

@RequiredArgsConstructor
@Repository
public class ArticleQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 게시글 통계 조회
     *
     * @param statisticRequest 통계 요청 정보
     * @return 통계 결과 리스트
     */
    public List<StatisticResponse> getStatistics(StatisticRequest statisticRequest) {
        // 통계 요청 타입(일별, 시간대별)에 맞는 날짜 템플릿 생성
        DateTemplate<String> dateTemplate = getDateTemplateByStatisticType(statisticRequest.getType());

        return queryFactory
                .select(
                        new QStatisticResponse(
                                dateTemplate,
                                getExpression(statisticRequest.getValue())
                        )
                )
                .from(hashtags)
                .join(hashtags.article, article)
                .where(
                        hashtags.name.eq(statisticRequest.getHashtag())
                                .and(article.createdAt.between(statisticRequest.getStart(), statisticRequest.getEnd()))
                )
                .groupBy(dateTemplate)
                .fetch();
    }

    /**
     * 통계 요청 타입(일별, 시간대별)에 맞는 날짜 템플릿 반환
     *
     * @param type date, hour
     * @return 날짜 템플릿
     */
    private DateTemplate<String> getDateTemplateByStatisticType(StatisticType type) {
        String template = type == StatisticType.DATE
                ? "DATE_FORMAT({0}, '%Y-%m-%d')" // 일별 통계의 경우 날짜만 반환하는 템플릿
                : "DATE_FORMAT({0}, '%Y-%m-%d %H:00')"; // 시간대별 통계의 경우 시간까지 반환하는 템플릿

        return Expressions.dateTemplate(String.class, template, article.createdAt);
    }

    /**
     * 통계 요청 값(게시글 수, 좋아요 수 등)에 맞는 표현식을 반환
     *
     * @param value 통계 값 타입 (COUNT, LIKE_COUNT, SHARE_COUNT, VIEW_COUNT)
     * @return
     */
    private NumberExpression<Integer> getExpression(StatisticValue value) {
        return switch (value) {
            case COUNT -> article.count().intValue();
            case LIKE_COUNT -> article.likeCount.sum();
            case SHARE_COUNT -> article.shareCount.sum();
            case VIEW_COUNT -> article.viewCount.sum();
            default -> throw new IllegalArgumentException(INVALID_STATISTIC_VALUE);
        };
    }
}
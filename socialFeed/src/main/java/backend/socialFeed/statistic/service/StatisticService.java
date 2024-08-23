package backend.socialFeed.statistic.service;

import backend.socialFeed.statistic.dto.StatisticRequest;
import backend.socialFeed.statistic.dto.StatisticResponse;

import java.util.List;

public interface StatisticService {
    List<StatisticResponse> getStatistics(StatisticRequest statisticRequest);
}

package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.UserStatistics;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserStatisticsRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link RankingList} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class RankingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RankingService.class);

  private final RankingListRepository rankingListRepository;

  private final UserStatisticsRepository userStatisticsRepository;

  @Autowired
  public RankingService(RankingListRepository rankingListRepository, UserStatisticsRepository userStatisticsRepository) {
    this.rankingListRepository = rankingListRepository;
    this.userStatisticsRepository = userStatisticsRepository;
  }

  public List<Ranking> findAllWithRankingScope(RankingScope rankingScope) {
    List<RankingList> rankingLists = rankingListRepository.findByRankingScopeOrderByCalculationDateDesc(rankingScope);

    if (!rankingLists.isEmpty()) {
      RankingList rankingList = rankingLists.get(0);
      LOGGER.debug("Returning rankings calculated at " + rankingList.calculationDate.toString());
      return rankingList.getRankings();
    }

    LOGGER.debug("No ranking list found for scope " + rankingScope + " - no rankings found.");
    return new ArrayList<>();
  }

  public void recalculateRankings() {
    ZonedDateTime now = ZonedDateTime.now();

    for (RankingScope rankingScope : RankingScope.values()) {
      rankingListRepository.save(new RankingList(
          calculateRankingsForScope(rankingScope),
          now,
          rankingScope));
      deleteRankingListsOlderThan(now, rankingScope);
    }
  }

  private void deleteRankingListsOlderThan(ZonedDateTime calculationDate, RankingScope rankingScope) {
    List<RankingList> rankingsToDelete = rankingListRepository.findByCalculationDateBeforeAndRankingScope(
        calculationDate, rankingScope);
    rankingListRepository.delete(rankingsToDelete);
  }

  private List<Ranking> calculateRankingsForScope(RankingScope rankingScope) {

    List<UserStatistics> userStatistics = userStatisticsRepository.findAll();

    List<Ranking> rankingsForScope = userStatistics
      .stream()
      .map(us -> us.getRanking(rankingScope))
      .sorted((r1, r2) -> r2.closedCount.compareTo(r1.closedCount))
      .collect(Collectors.toList());

    for (int rank = 0; rank < rankingsForScope.size(); rank++) {
      rankingsForScope.get(rank).rank = rank + 1;
    }

    return rankingsForScope;
  }
}

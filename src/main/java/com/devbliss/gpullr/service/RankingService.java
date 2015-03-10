package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.repository.RankingListRepository;
import java.util.ArrayList;
import java.util.List;
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

  private final RankingListRepository rankingRepository;

  @Autowired
  public RankingService(RankingListRepository rankingListRepository) {
    this.rankingRepository = rankingListRepository;
  }

  public List<Ranking> findAllWithRankingScope(RankingScope rankingScope) {
    List<RankingList> rankingLists = rankingRepository.findByRankingScopeOrderByCalculationDateDesc(rankingScope);

    if (!rankingLists.isEmpty()) {
      RankingList rankingList = rankingLists.get(0);
      LOGGER.debug("Returning rankings calculated at " + rankingList.calculationDate.toString());
      return rankingList.getRankings();
    }

    LOGGER.debug("No ranking list found for scope " + rankingScope + " - no rankings found.");
    return new ArrayList<>();
  }
}

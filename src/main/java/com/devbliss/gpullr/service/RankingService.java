package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.repository.RankingRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link Ranking} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class RankingService {

  private static final int RETRY_AFTER_MILLIS_IF_EMPTY = 1000;

  private final RankingRepository rankingRepository;

  @Autowired
  public RankingService(RankingRepository rankingRepository) {
    this.rankingRepository = rankingRepository;
  }

  /**
   * Removes ALL rankings from storage and inserts given rankings.
   * For simplicity and performance reasons, this method is NOT really thread-safe.
   * 
   * That means, for a short period
   * 
   * @param rankings
   */
  public void replace(List<Ranking> rankings) {
    rankingRepository.deleteAll();
    rankingRepository.save(rankings);
  }

  public List<Ranking> findAllWithRankingScope(RankingScope rankingScope) {
    List<Ranking> rankings = rankingRepository.findByRankingScopeOrderByNumberOfMergedPullRequests(rankingScope);

    // dirty but should do: if this is called while replace() is working - let's just wait and try
    // again:
    if (rankings.isEmpty()) {
      try {
        Thread.sleep(RETRY_AFTER_MILLIS_IF_EMPTY);
      } catch (InterruptedException e) {
        throw new UnexpectedException(e);
      }

      rankings = rankingRepository.findByRankingScopeOrderByNumberOfMergedPullRequests(rankingScope);
    }

    return rankings;
  }
}

package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserStatistics;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.repository.UserStatisticsRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class RankingServiceIntegrationTest {

  @Autowired
  private RankingListRepository rankingListRepository;

  @Autowired
  private UserStatisticsRepository userStatisticsRepository;

  @Autowired
  private UserRepository userRepository;

  private PullRequest pullRequest;

  private RankingService rankingService;

  private User userAlpha;

  private User userBeta;

  private User userGamma;

  @Before
  public void setup() {
    pullRequest = mock(PullRequest.class);
    rankingService = new RankingService(rankingListRepository, userStatisticsRepository);

    // create 3 users:
    userAlpha = userRepository.save(new User(14, "alpha", ""));
    userBeta = userRepository.save(new User(13, "beta", ""));
    userGamma = userRepository.save(new User(17, "gamma", ""));

    // create statistics for these users and fill them with some closed pull requests:
    UserStatistics userStatisticsAlpha = new UserStatistics(userAlpha);
    userStatisticsAlpha.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusHours(1));
    userStatisticsAlpha.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusHours(2));
    userStatisticsAlpha.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusHours(3));
    userStatisticsAlpha.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(2));
    userStatisticsAlpha.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(8));
    userStatisticsAlpha.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(28));
    userStatisticsAlpha.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(42));

    UserStatistics userStatisticsBeta = new UserStatistics(userBeta);
    userStatisticsBeta.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusHours(4));
    userStatisticsBeta.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(1));
    userStatisticsBeta.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(2));
    userStatisticsBeta.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(3));
    userStatisticsBeta.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(4));
    userStatisticsBeta.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(5));

    UserStatistics userStatisticsGamma = new UserStatistics(userGamma);
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(4));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(9));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(10));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(11));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(32));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(33));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(34));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(35));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(36));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(34));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(38));
    userStatisticsGamma.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(41));

    userStatisticsRepository.save(userStatisticsAlpha);
    userStatisticsRepository.save(userStatisticsBeta);
    userStatisticsRepository.save(userStatisticsGamma);
  }

  @After
  public void teardown() {
    userStatisticsRepository.deleteAll();
    userRepository.deleteAll();
    rankingListRepository.deleteAll();
  }

  @Test
  public void noRankingsWithoutCalculation() {
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.TODAY).isPresent());
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.LAST_7_DAYS).isPresent());
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.LAST_30_DAYS).isPresent());
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.ALL_TIME).isPresent());
  }

  @Test
  public void rankingsForToday() {
    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.TODAY); 
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());

    // according to the user statistics setup, the ranking should be: [alpha, beta, gamma]:
    assertEquals(userAlpha.username, rankings.get(0).username);
    assertEquals(userBeta.username, rankings.get(1).username);
    assertEquals(userGamma.username, rankings.get(2).username);

    // the number of pull requests for the rankings should be [3, 1, 0]:
    assertEquals(3, rankings.get(0).closedCount.longValue());
    assertEquals(1, rankings.get(1).closedCount.longValue());
    assertEquals(0, rankings.get(2).closedCount.longValue());

    // and the numbers of the rankings should be [1, 2, 3], of course:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
  }

  @Test
  public void rankingsForLast7Days() {
    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.LAST_7_DAYS); 
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());

    // according to the user statistics setup, the ranking should be: [beta, alpha, gamma]:
    assertEquals(userBeta.username, rankings.get(0).username);
    assertEquals(userAlpha.username, rankings.get(1).username);
    assertEquals(userGamma.username, rankings.get(2).username);

    // the number of pull requests for the rankings should be [6, 4, 1]:
    assertEquals(6, rankings.get(0).closedCount.longValue());
    assertEquals(4, rankings.get(1).closedCount.longValue());
    assertEquals(1, rankings.get(2).closedCount.longValue());

    // and the numbers of the rankings should be [1, 2, 3], of course:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
  }

  @Test
  public void rankingsForLast30Days() {
    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.LAST_30_DAYS); 
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());

    // according to the user statistics setup, the ranking should be: [alpha, beta, gamma]
    // (alphabetical if same rank):
    assertEquals(userAlpha.username, rankings.get(0).username);
    assertEquals(userBeta.username, rankings.get(1).username);
    assertEquals(userGamma.username, rankings.get(2).username);

    // the number of pull requests for the rankings should be [6, 6, 4]:
    assertEquals(6, rankings.get(0).closedCount.longValue());
    assertEquals(6, rankings.get(1).closedCount.longValue());
    assertEquals(4, rankings.get(2).closedCount.longValue());

    // and the numbers of the rankings should be [1, 2, 3], of course:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
  }

  @Test
  public void rankingsForLastAllTime() {
    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME); 
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());

    // according to the user statistics setup, the ranking should be: [alpha, beta, gamma]
    // (alphabetical if same rank):
    assertEquals(userGamma.username, rankings.get(0).username);
    assertEquals(userAlpha.username, rankings.get(1).username);
    assertEquals(userBeta.username, rankings.get(2).username);

    // the number of pull requests for the rankings should be [12, 7, 6]:
    assertEquals(12, rankings.get(0).closedCount.longValue());
    assertEquals(7, rankings.get(1).closedCount.longValue());
    assertEquals(6, rankings.get(2).closedCount.longValue());

    // and the numbers of the rankings should be [1, 2, 3], of course:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
  }

  @Test
  public void alwaysLatestRanking() {
    // trigger ranking calculation and fetch:
    rankingService.recalculateRankings();
    Optional<RankingList> ranking = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(ranking.isPresent());
    ZonedDateTime firstCalcDate = ranking.get().calculationDate;
    
    // wait a moment:
    try {
      Thread.sleep(1250);
    } catch(InterruptedException e) {
      
    }
    
    // trigger ranking calculation again and fetch again:
    rankingService.recalculateRankings();
    ranking = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(ranking.isPresent());
    ZonedDateTime secondCalcDate = ranking.get().calculationDate;
    
    // second fetch should have returned a newer ranking list:
    assertTrue(firstCalcDate.isBefore(secondCalcDate));
  } 
}

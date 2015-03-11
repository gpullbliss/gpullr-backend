package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserHasClosedPullRequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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

  private Random random;

  @Autowired
  private RankingListRepository rankingListRepository;

  @Autowired
  private UserHasClosedPullRequestRepository userHasClosedPullRequestRepository;

  @Autowired
  private UserRepository userRepository;

  private RankingService rankingService;

  private User userAlpha;

  private User userBeta;

  private User userGamma;

  @Before
  public void setup() {
    random = new Random();
    rankingService = new RankingService(rankingListRepository, userHasClosedPullRequestRepository, userRepository);

    // create 3 users:
    userAlpha = userRepository.save(new User(14, "alpha", "http://alpha"));
    userBeta = userRepository.save(new User(13, "Beta", "http://beta")); // intentionally upper case username!
    userGamma = userRepository.save(new User(17, "gamma", "http://gamma"));
  }

  @After
  public void teardown() {
    userHasClosedPullRequestRepository.deleteAll();
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
    createSomeClosedPullRequests();

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
  }

  @Test
  public void rankingsForLast7Days() {
    createSomeClosedPullRequests();

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
  }

  @Test
  public void rankingsForLast30Days() {
    createSomeClosedPullRequests();

    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.LAST_30_DAYS);
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());

    // according to the user statistics setup, the ranking should be: [alpha, beta, gamma]
    // (alphabetically ordered when same rank):
    assertEquals(userAlpha.username, rankings.get(0).username);
    assertEquals(userBeta.username, rankings.get(1).username);
    assertEquals(userGamma.username, rankings.get(2).username);

    // the number of pull requests for the rankings should be [6, 6, 4]:
    assertEquals(6, rankings.get(0).closedCount.longValue());
    assertEquals(6, rankings.get(1).closedCount.longValue());
    assertEquals(4, rankings.get(2).closedCount.longValue());
  }

  @Test
  public void rankingsForLastAllTime() {
    createSomeClosedPullRequests();

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
    } catch (InterruptedException e) {

    }

    // trigger ranking calculation again and fetch again:
    rankingService.recalculateRankings();
    ranking = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(ranking.isPresent());
    ZonedDateTime secondCalcDate = ranking.get().calculationDate;

    // second fetch should have returned a newer ranking list:
    assertTrue(firstCalcDate.isBefore(secondCalcDate));
  }

  @Test
  public void submittingClosedPullRequestTwiceDoesNotCountTwice() {
    // at the beginning, there is no ranking at all:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertFalse(rankingList.isPresent());

    // submitting a closed pull request and trigger calculation:
    PullRequest pullRequest = createPullRequest(userAlpha, ZonedDateTime.now().minusHours(2));
    rankingService.userHasClosedPullRequest(pullRequest);
    rankingService.recalculateRankings();

    // fetching rankings - which should reflect the closed pull request:
    rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());
    assertEquals(1, rankings.get(0).closedCount.longValue());
    assertEquals(0, rankings.get(1).closedCount.longValue());
    assertEquals(0, rankings.get(2).closedCount.longValue());
    assertEquals(userAlpha.username, rankings.get(0).username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).avatarUrl);

    // submitting same pull request again and trigger calculation:
    rankingService.userHasClosedPullRequest(pullRequest);
    rankingService.recalculateRankings();

    // fetching rankings - result should not have changed:
    rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());
    assertEquals(1, rankings.get(0).closedCount.longValue());
    assertEquals(0, rankings.get(1).closedCount.longValue());
    assertEquals(0, rankings.get(2).closedCount.longValue());
    assertEquals(userAlpha.username, rankings.get(0).username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).avatarUrl);

    // submitting same pull request again - this time with different assignee - and trigger
    // calculation:
    pullRequest.assignee = userBeta;
    rankingService.userHasClosedPullRequest(pullRequest);
    rankingService.recalculateRankings();

    // fetching rankings - result should not have changed:
    rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());
    assertEquals(1, rankings.get(0).closedCount.longValue());
    assertEquals(0, rankings.get(1).closedCount.longValue());
    assertEquals(0, rankings.get(2).closedCount.longValue());
    assertEquals(userAlpha.username, rankings.get(0).username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).avatarUrl);
  }

  private PullRequest createPullRequest(User assignee, ZonedDateTime closeDate) {
    PullRequest pullRequest = new PullRequest();
    pullRequest.assignee = assignee;
    pullRequest.closedAt = closeDate;
    StringBuilder randomPart = new StringBuilder("http://someurl/");

    for (int i = 0; i < 20; i++) {
      randomPart.append(random.nextInt(10));
    }

    pullRequest.url = randomPart.toString();
    return pullRequest;
  }

  private void createSomeClosedPullRequests() {
    rankingService.userHasClosedPullRequest(createPullRequest(userAlpha, ZonedDateTime.now().minusHours(1)));
    rankingService.userHasClosedPullRequest(createPullRequest(userAlpha, ZonedDateTime.now().minusHours(2)));
    rankingService.userHasClosedPullRequest(createPullRequest(userAlpha, ZonedDateTime.now().minusHours(3)));
    rankingService.userHasClosedPullRequest(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(2)));
    rankingService.userHasClosedPullRequest(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(8)));
    rankingService.userHasClosedPullRequest(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(28)));
    rankingService.userHasClosedPullRequest(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(42)));

    rankingService.userHasClosedPullRequest(createPullRequest(userBeta, ZonedDateTime.now().minusHours(4)));
    rankingService.userHasClosedPullRequest(createPullRequest(userBeta, ZonedDateTime.now().minusDays(1)));
    rankingService.userHasClosedPullRequest(createPullRequest(userBeta, ZonedDateTime.now().minusDays(2)));
    rankingService.userHasClosedPullRequest(createPullRequest(userBeta, ZonedDateTime.now().minusDays(3)));
    rankingService.userHasClosedPullRequest(createPullRequest(userBeta, ZonedDateTime.now().minusDays(4)));
    rankingService.userHasClosedPullRequest(createPullRequest(userBeta, ZonedDateTime.now().minusDays(5)));

    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(4)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(9)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(10)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(11)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(32)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(33)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(34)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(35)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(36)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(34)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(38)));
    rankingService.userHasClosedPullRequest(createPullRequest(userGamma, ZonedDateTime.now().minusDays(41)));
  }
}

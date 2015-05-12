package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.RepoRepository;
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
 * Integration test that tests ranking service with non-mocked persistence layer.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class RankingServiceIntegrationTest {

  /**
   * Score of the example pull request used in this test.
   */
  private static final double PULLREQUEST_SCORE = 14d;

  private static final double COMPARISON_ACCURACY = .01;

  private Random random;

  @Autowired
  private RankingListRepository rankingListRepository;

  @Autowired
  private PullRequestRepository pullRequestRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RepoRepository repoRepository;

  private RankingService rankingService;

  private User userAlpha;

  private User userBeta;

  private User userGamma;

  private Repo repo;

  private User author;

  @Before
  public void setup() {
    random = new Random();
    rankingService = new RankingService(rankingListRepository, pullRequestRepository, userRepository);

    // create 3 users that close pull requests:
    userAlpha = createUser(14, "zzz_alpha", true, "Alpha");
    userBeta = createUser(13, "Beta", true, null); // yes, upper case!
    userGamma = createUser(17, "gamma", true, null);

    // and one author of all pull requests:
    author = createUser(1, "megaauthor", true, null);

    // and one repo for all pull requests:
    repo = repoRepository.save(new Repo(1, "Some Repo", "Some description"));
  }

  @After
  public void teardown() {
    pullRequestRepository.deleteAll();
    rankingListRepository.deleteAll();
    repoRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void noRankingsWithoutCalculation() {
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.TODAY).isPresent());
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.LAST_7_DAYS).isPresent());
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.LAST_30_DAYS).isPresent());
    assertFalse(rankingService.findAllWithRankingScope(RankingScope.ALL_TIME).isPresent());
  }

  @Test
  public void noRankingsWithZeroClosedCount() {
    // create exactly ONE closed pullrequest and trigger ranking calculation:
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusHours(1)));
    rankingService.recalculateRankings();

    // verify that only one ranking is returned, no zero-closed-count ranking for the other users:
    Optional<RankingList> allWithRankingScope = rankingService.findAllWithRankingScope(RankingScope.TODAY);
    assertTrue(allWithRankingScope.isPresent());
    List<Ranking> rankings = allWithRankingScope.get().getRankings();
    assertEquals(1, rankings.size());
    assertEquals(1 * PULLREQUEST_SCORE, rankings.get(0).getScore().doubleValue(), COMPARISON_ACCURACY);
    assertEquals(1, rankings.size());
    assertEquals(userAlpha, rankings.get(0).getUser());
  }

  @Test
  public void dontCalculateRankingsForUsersThatAssignedThemselves() {
    // create four pull requests
    pullRequestRepository.save(createPullRequest(author, ZonedDateTime.now().minusHours(1)));
    pullRequestRepository.save(createPullRequest(author, ZonedDateTime.now().minusHours(2)));
    pullRequestRepository.save(createPullRequest(author, ZonedDateTime.now().minusHours(3)));
    pullRequestRepository.save(createPullRequest(author, ZonedDateTime.now().minusHours(4)));

    // trigger ranking calculation:
    rankingService.recalculateRankings();

    Optional<RankingList> allWithRankingScope = rankingService.findAllWithRankingScope(RankingScope.TODAY);
    assertTrue(allWithRankingScope.isPresent());
    List<Ranking> rankings = allWithRankingScope.get().getRankings();
    rankings.forEach(r -> assertTrue(r.getScore() == 0L));
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
    assertEquals(2, rankings.size());

    // according to the user statistics setup, the ranking should be: [alpha, beta, gamma,
    // megaauthor]:
    assertEquals(userAlpha.username, rankings.get(0).getUser().username);
    assertEquals(userBeta.username, rankings.get(1).getUser().username);

    // the number of pull requests for the rankings should be [3, 1]:
    assertTrue(rankings.get(0).getScore().doubleValue() > rankings.get(1).getScore().doubleValue());
    assertEquals(3, rankings.get(0).getClosedCount());
    assertEquals(1, rankings.get(1).getClosedCount());

    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).getRank());
    assertEquals(2, rankings.get(1).getRank());
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

    // according to the user statistics setup, the ranking should be: [beta, alpha, gamma,
    // megaauthor]:
    assertEquals(userBeta.username, rankings.get(0).getUser().username);
    assertEquals(userAlpha.username, rankings.get(1).getUser().username);
    assertEquals(userGamma.username, rankings.get(2).getUser().username);

    // the number of pull requests for the rankings should be [6, 4, 1]:
    assertTrue(rankings.get(0).getScore().doubleValue() > rankings.get(1).getScore().doubleValue());
    assertTrue(rankings.get(1).getScore().doubleValue() > rankings.get(2).getScore().doubleValue());
    assertEquals(6, rankings.get(0).getClosedCount());
    assertEquals(4, rankings.get(1).getClosedCount());
    assertEquals(1, rankings.get(2).getClosedCount());

    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).getRank());
    assertEquals(2, rankings.get(1).getRank());
    assertEquals(3, rankings.get(2).getRank());
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

    // according to the user statistics setup, the ranking should be: [alpha, beta, gamma]:
    assertEquals(userBeta.username, rankings.get(0).getUser().username);
    assertEquals(userAlpha.username, rankings.get(1).getUser().username);
    assertEquals(userGamma.username, rankings.get(2).getUser().username);

    // the number of pull requests for the rankings should be [6, 6, 4]:
    assertEquals(rankings.get(0).getScore().doubleValue(), rankings.get(1).getScore().doubleValue(),
        COMPARISON_ACCURACY);
    assertTrue(rankings.get(1).getScore().doubleValue() > rankings.get(2).getScore().doubleValue());
    assertEquals(6, rankings.get(0).getClosedCount());
    assertEquals(6, rankings.get(1).getClosedCount());
    assertEquals(4, rankings.get(2).getClosedCount());

    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).getRank());
    assertEquals(1, rankings.get(1).getRank());
    assertEquals(2, rankings.get(2).getRank());
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

    // according to the user statistics setup, the ranking should be: [gamma, alpha, beta]
    assertEquals(userGamma.username, rankings.get(0).getUser().username);
    assertEquals(userAlpha.username, rankings.get(1).getUser().username);
    assertEquals(userBeta.username, rankings.get(2).getUser().username);

    // the number of pull requests for the rankings should be [12, 7, 6]:
    assertTrue(rankings.get(0).getScore().doubleValue() > rankings.get(1).getScore().doubleValue());
    assertTrue(rankings.get(1).getScore().doubleValue() > rankings.get(2).getScore().doubleValue());
    assertEquals(12, rankings.get(0).getClosedCount());
    assertEquals(7, rankings.get(1).getClosedCount());
    assertEquals(6, rankings.get(2).getClosedCount());

    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).getRank());
    assertEquals(2, rankings.get(1).getRank());
    assertEquals(3, rankings.get(2).getRank());
  }

  @Test
  public void alwaysLatestRanking() throws Exception {
    // trigger ranking calculation and fetch:
    rankingService.recalculateRankings();
    Optional<RankingList> ranking = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(ranking.isPresent());
    ZonedDateTime firstCalcDate = ranking.get().calculationDate;

    // wait a moment:
    Thread.sleep(1250);

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
    pullRequestRepository.save(pullRequest);
    rankingService.recalculateRankings();

    // fetching rankings - which should reflect the closed pull request:
    rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(1, rankings.size());
    assertEquals(1 * PULLREQUEST_SCORE, rankings.get(0).getScore().doubleValue(), COMPARISON_ACCURACY);

    assertEquals(userAlpha.username, rankings.get(0).getUser().username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).getUser().avatarUrl);

    // submitting same pull request again and trigger calculation:
    pullRequestRepository.save(pullRequest);
    rankingService.recalculateRankings();

    // fetching rankings - result should not have changed:
    rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    rankings = rankingList.get().getRankings();

    assertEquals(1, rankings.size());
    assertEquals(1 * PULLREQUEST_SCORE, rankings.get(0).getScore().doubleValue(), COMPARISON_ACCURACY);
    assertEquals(userAlpha.username, rankings.get(0).getUser().username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).getUser().avatarUrl);

    // submitting same pull request again - this time with different assignee - and trigger
    // calculation:
    pullRequest = createPullRequest(userBeta, ZonedDateTime.now().minusHours(2));
    pullRequestRepository.save(pullRequest);
    rankingService.recalculateRankings();

    // fetching rankings - this time ranking should have changed:
    rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    rankings = rankingList.get().getRankings();

    assertEquals(2, rankings.size());
    assertEquals(1 * PULLREQUEST_SCORE, rankings.get(0).getScore().doubleValue(), COMPARISON_ACCURACY);
    assertEquals(userBeta.username, rankings.get(0).getUser().username);
    assertEquals(userBeta.avatarUrl, rankings.get(0).getUser().avatarUrl);
    assertEquals(userAlpha.username, rankings.get(1).getUser().username);
    assertEquals(userAlpha.avatarUrl, rankings.get(1).getUser().avatarUrl);
  }

  @Test
  public void dontCalculateRankingsForUsersThatDontBelongToUs() {
    // create a user that is NOT part of our company:
    final int strangerId = 19;
    User stranger = createUser(strangerId, "stranger", false, null);

    createSomeClosedPullRequests();

    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();

    // but there should only be rankings for the users belonging to us:
    assertEquals(3, rankings.size());
    rankings.forEach(r -> {
      assertFalse("User not belonging to us should not have a ranking", stranger.equals(r.getUser()));
    });
  }

  private PullRequest createPullRequest(User assignee, ZonedDateTime closeDate) {
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = random.nextInt(Integer.MAX_VALUE);
    pullRequest.assignee = assignee;
    pullRequest.closedAt = closeDate;
    pullRequest.state = State.CLOSED;
    pullRequest.author = author;
    pullRequest.repo = repo;
    StringBuilder randomPart = new StringBuilder("http://someurl/");

    for (int i = 0; i < 20; i++) {
      randomPart.append(random.nextInt(10));
    }

    pullRequest.url = randomPart.toString();

    // make sure all pull requests have same score value (PULLREQUEST_SCORE):
    pullRequest.linesAdded = 100;
    pullRequest.linesRemoved = 98;
    pullRequest.filesChanged = 2;
    pullRequest.numberOfComments = 2;
    return pullRequest;
  }

  private void createSomeClosedPullRequests() {
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusHours(1)));
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusHours(2)));
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusHours(3)));
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(2)));
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(8)));
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(28)));
    pullRequestRepository.save(createPullRequest(userAlpha, ZonedDateTime.now().minusDays(42)));

    pullRequestRepository.save(createPullRequest(userBeta, ZonedDateTime.now().minusHours(4)));
    pullRequestRepository.save(createPullRequest(userBeta, ZonedDateTime.now().minusDays(1)));
    pullRequestRepository.save(createPullRequest(userBeta, ZonedDateTime.now().minusDays(2)));
    pullRequestRepository.save(createPullRequest(userBeta, ZonedDateTime.now().minusDays(3)));
    pullRequestRepository.save(createPullRequest(userBeta, ZonedDateTime.now().minusDays(4)));
    pullRequestRepository.save(createPullRequest(userBeta, ZonedDateTime.now().minusDays(5)));

    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(4)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(9)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(10)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(11)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(32)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(33)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(34)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(35)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(36)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(34)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(38)));
    pullRequestRepository.save(createPullRequest(userGamma, ZonedDateTime.now().minusDays(41)));

    rankingService.recalculateRankings();
  }

  private User createUser(Integer id, String username, Boolean canLogin, String fullName) {
    User user = new User(id, username, fullName, null, canLogin, null, null);
    return userRepository.save(user);
  }
}

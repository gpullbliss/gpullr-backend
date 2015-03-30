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
    userAlpha = createUser(14, "alpha", true);
    userBeta = createUser(13, "Beta", true); // yes, upper case!
    userGamma = createUser(17, "gamma", true);

    // and one author of all pull requests:
    author = createUser(1, "megaauthor", true);

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
  public void rankingsForToday() {
    createSomeClosedPullRequests();

    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.TODAY);
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();
    assertEquals(3, rankings.size());

    // according to the user statistics setup, the ranking should be: [alpha, beta, gamma,
    // megaauthor]:
    assertEquals(1, rankings.get(0).users.size());
    assertEquals(1, rankings.get(1).users.size());
    assertEquals(2, rankings.get(2).users.size());

    assertEquals(userAlpha.username, rankings.get(0).users.get(0).username);
    assertEquals(userBeta.username, rankings.get(1).users.get(0).username);
    assertEquals(userGamma.username, rankings.get(2).users.get(0).username);
    assertEquals(author.username, rankings.get(2).users.get(1).username);

    // the number of pull requests for the rankings should be [3, 1, 0]:
    assertEquals(3, rankings.get(0).closedCount.longValue());
    assertEquals(1, rankings.get(1).closedCount.longValue());
    assertEquals(0, rankings.get(2).closedCount.longValue());
    
    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
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
    assertEquals(4, rankings.size());

    // according to the user statistics setup, the ranking should be: [beta, alpha, gamma,
    // megaauthor]:
    assertEquals(1, rankings.get(0).users.size());
    assertEquals(1, rankings.get(1).users.size());
    assertEquals(1, rankings.get(2).users.size());
    assertEquals(1, rankings.get(3).users.size());

    assertEquals(userBeta.username, rankings.get(0).users.get(0).username);
    assertEquals(userAlpha.username, rankings.get(1).users.get(0).username);
    assertEquals(userGamma.username, rankings.get(2).users.get(0).username);
    assertEquals(author.username, rankings.get(3).users.get(0).username);

    // the number of pull requests for the rankings should be [6, 4, 1]:
    assertEquals(6, rankings.get(0).closedCount.longValue());
    assertEquals(4, rankings.get(1).closedCount.longValue());
    assertEquals(1, rankings.get(2).closedCount.longValue());
    
    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
    assertEquals(4, rankings.get(3).rank.intValue());
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
    assertEquals(2, rankings.get(0).users.size());
    assertEquals(1, rankings.get(1).users.size());
    assertEquals(1, rankings.get(2).users.size());

    assertEquals(userAlpha.username, rankings.get(0).users.get(0).username);
    assertEquals(userBeta.username, rankings.get(0).users.get(1).username);
    assertEquals(userGamma.username, rankings.get(1).users.get(0).username);
    assertEquals(author.username, rankings.get(2).users.get(0).username);

    // the number of pull requests for the rankings should be [6, 6, 4]:
    assertEquals(6, rankings.get(0).closedCount.longValue());
    assertEquals(4, rankings.get(1).closedCount.longValue());
    assertEquals(0, rankings.get(2).closedCount.longValue());
    
    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
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
    assertEquals(4, rankings.size());

    // according to the user statistics setup, the ranking should be: [gamma, alpha, beta]
    assertEquals(1, rankings.get(0).users.size());
    assertEquals(1, rankings.get(1).users.size());
    assertEquals(1, rankings.get(2).users.size());
    assertEquals(1, rankings.get(3).users.size());

    assertEquals(userGamma.username, rankings.get(0).users.get(0).username);
    assertEquals(userAlpha.username, rankings.get(1).users.get(0).username);
    assertEquals(userBeta.username, rankings.get(2).users.get(0).username);
    assertEquals(author.username, rankings.get(3).users.get(0).username);

    // the number of pull requests for the rankings should be [12, 7, 6]:
    assertEquals(12, rankings.get(0).closedCount.longValue());
    assertEquals(7, rankings.get(1).closedCount.longValue());
    assertEquals(6, rankings.get(2).closedCount.longValue());
    
    // the numeric rank values should be set in ascending order starting with 0:
    assertEquals(1, rankings.get(0).rank.intValue());
    assertEquals(2, rankings.get(1).rank.intValue());
    assertEquals(3, rankings.get(2).rank.intValue());
    assertEquals(4, rankings.get(3).rank.intValue());
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
    assertEquals(2, rankings.size());
    assertEquals(1, rankings.get(0).closedCount.longValue());
    assertEquals(0, rankings.get(1).closedCount.longValue());

    assertEquals(1, rankings.get(0).users.size());
    assertEquals(3, rankings.get(1).users.size());
    assertEquals(userAlpha.username, rankings.get(0).users.get(0).username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).users.get(0).avatarUrl);

    // submitting same pull request again and trigger calculation:
    pullRequestRepository.save(pullRequest);
    rankingService.recalculateRankings();

    // fetching rankings - result should not have changed:
    rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    rankings = rankingList.get().getRankings();

    assertEquals(2, rankings.size());
    assertEquals(1, rankings.get(0).closedCount.longValue());
    assertEquals(0, rankings.get(1).closedCount.longValue());
    assertEquals(1, rankings.get(0).users.size());
    assertEquals(3, rankings.get(1).users.size());
    assertEquals(userAlpha.username, rankings.get(0).users.get(0).username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).users.get(0).avatarUrl);
    assertEquals(userBeta.username, rankings.get(1).users.get(0).username);
    assertEquals(userBeta.avatarUrl, rankings.get(1).users.get(0).avatarUrl);

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
    assertEquals(1, rankings.get(0).closedCount.longValue());
    assertEquals(0, rankings.get(1).closedCount.longValue());
    assertEquals(2, rankings.get(0).users.size());
    assertEquals(userAlpha.username, rankings.get(0).users.get(0).username);
    assertEquals(userAlpha.avatarUrl, rankings.get(0).users.get(0).avatarUrl);
    assertEquals(userBeta.username, rankings.get(0).users.get(1).username);
    assertEquals(userBeta.avatarUrl, rankings.get(0).users.get(1).avatarUrl);
  }

  @Test
  public void dontCalculateRankingsForUsersThatDontBelongToUs() {
    // create a user that is NOT part of our company:
    final int strangerId = 19;
    User stranger = createUser(strangerId, "stranger", false);

    createSomeClosedPullRequests();

    // trigger ranking calculation:
    rankingService.recalculateRankings();

    // fetch calculated rankings:
    Optional<RankingList> rankingList = rankingService.findAllWithRankingScope(RankingScope.ALL_TIME);
    assertTrue(rankingList.isPresent());
    List<Ranking> rankings = rankingList.get().getRankings();

    // but there should only be rankings for the users belonging to us:
    assertEquals(4, rankings.size());
    rankings.forEach(r -> {
      r.users.forEach(u -> assertFalse("User not belonging to us should not have a ranking", stranger.equals(u)));
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
  }

  private User createUser(Integer id, String username, Boolean canLogin) {
    User user = new User(id, username, null, null, canLogin, null);
    return userRepository.save(user);
  }
}

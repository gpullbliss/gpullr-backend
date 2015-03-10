package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserStatistics;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.repository.UserStatisticsRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
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
public class UserStatisticsServiceIntegrationTest {

  private static final ZonedDateTime CLOSED_AT = ZonedDateTime.now().minusDays(2);

  private static final Integer USER_ID = 17;

  @Autowired
  private UserStatisticsRepository userStatisticsRepository;

  @Autowired
  private RankingListRepository rankingListRepository;

  @Autowired
  private UserRepository userRepository;

  private RankingService rankingService;

  private User assignee;

  private UserStatisticsService userStatisticsService;

  private PullRequest pullRequest;

  @Before
  public void setup() {
    rankingService = mock(RankingService.class);
    assignee = new User(USER_ID, "someUser", "http://google.de");
    userRepository.save(assignee);
    pullRequest = new PullRequest();
    pullRequest.assignee = assignee;
    userStatisticsService = new UserStatisticsService(userStatisticsRepository, rankingService, userRepository);
  }

  @Test
  public void userStatisticsAreRefreshedAndRankingsAReRecalculated() {
    // at the beginning, there is no statistics for the user:
    assertFalse(userStatisticsRepository.findByUserId(USER_ID).isPresent());

    // after a pull request has been closed, this is reflected in the statistics:
    userStatisticsService.pullRequestWasClosed(pullRequest, CLOSED_AT);
    Optional<UserStatistics> userStatistics = userStatisticsRepository.findByUserId(USER_ID);
    assertTrue(userStatistics.isPresent());
    assertEquals(USER_ID, userStatistics.get().user.id);
    assertEquals(0L,
        userStatistics.get().getRanking(RankingScope.TODAY).numberOfMergedPullRequests.longValue());
    assertEquals(
        1L,
        userStatistics.get().getRanking(RankingScope.LAST_7_DAYS).numberOfMergedPullRequests.longValue());
    verify(rankingService).recalculateRankings();

  }
}

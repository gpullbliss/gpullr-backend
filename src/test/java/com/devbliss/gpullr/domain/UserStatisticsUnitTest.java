package com.devbliss.gpullr.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserStatisticsUnitTest {

  private static final String USER_NAME = "hardWorkingUser";

  @Mock
  private PullRequest pullRequest;

  private User user;

  private UserStatistics userStatistics;

  @Before
  public void setup() {
    user = new User();
    user.username = USER_NAME;
    userStatistics = new UserStatistics(user);
  }

  @Test
  public void getRanking() {
    // add a closed PR today, 5 days ago, 23 days ago and 45 days ago:
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now());
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(5));
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(23));
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(45));

    // asking for all should return 4 - rank is not set here:
    Ranking ranking = userStatistics.getRanking(RankingScope.ALL_TIME);
    assertEquals(4, ranking.closedCount.longValue());
    assertNull(ranking.rank);
    assertEquals(USER_NAME, ranking.username);

    // asking for last 30 days should return 3 - rank is not set here:
    ranking = userStatistics.getRanking(RankingScope.LAST_30_DAYS);
    assertEquals(3, ranking.closedCount.longValue());
    assertNull(ranking.rank);
    assertEquals(USER_NAME, ranking.username);

    // asking for last 7 days should return 2 - rank is not set here:
    ranking = userStatistics.getRanking(RankingScope.LAST_7_DAYS);
    assertEquals(2, ranking.closedCount.longValue());
    assertNull(ranking.rank);
    assertEquals(USER_NAME, ranking.username);

    // asking for today should return 1 - rank is not set here:
    ranking = userStatistics.getRanking(RankingScope.TODAY);
    assertEquals(1, ranking.closedCount.longValue());
    assertNull(ranking.rank);
    assertEquals(USER_NAME, ranking.username);
  }
}

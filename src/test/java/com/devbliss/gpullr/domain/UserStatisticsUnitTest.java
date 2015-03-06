package com.devbliss.gpullr.domain;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public class UserStatisticsUnitTest {
  
  @Mock
  private PullRequest pullRequest;
  
  private UserStatistics userStatistics;
  
  @Before
  public void setup() {
    userStatistics = new UserStatistics();
  }
  
  @Test
  public void getNumberOfClosedPullRequests() {
    // add a closed PR today, 5 days ago, 23 days ago and 45 days ago:
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now());
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(5));
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(23));
    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now().minusDays(45));
    
    // asking for all should return 4:
    assertEquals(4, userStatistics.getNumberOfClosedPullRequests(RankingScope.ALL_TIME));
    
    // asking for last 30 days should return 3:
    assertEquals(3, userStatistics.getNumberOfClosedPullRequests(RankingScope.LAST_30_DAYS));
    
    // asking for last 7 days should return 2:
    assertEquals(2, userStatistics.getNumberOfClosedPullRequests(RankingScope.LAST_7_DAYS));
    
    // asking for today should return 1:
    assertEquals(1, userStatistics.getNumberOfClosedPullRequests(RankingScope.TODAY));
  }
}

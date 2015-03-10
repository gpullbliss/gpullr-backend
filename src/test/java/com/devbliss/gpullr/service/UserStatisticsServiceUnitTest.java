package com.devbliss.gpullr.service;

import com.devbliss.gpullr.repository.UserRepository;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserStatistics;
import com.devbliss.gpullr.repository.RankingListRepository;
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
public class UserStatisticsServiceUnitTest {

  private static final ZonedDateTime CLOSED_AT = ZonedDateTime.now().minusDays(2);

  @Autowired
  private UserStatisticsRepository userStatisticsRepository;

  @Autowired
  private RankingListRepository rankingListRepository;
  
  @Autowired
  private UserRepository userRepository;

  private User assignee;

  private UserStatisticsService userStatisticsService;

  private PullRequest pullRequest;

  @Before
  public void setup() {
    assignee = new User(1, "someUser", "http://google.de");
    userRepository.save(assignee);
    pullRequest = new PullRequest();
    pullRequest.assignee = assignee;
    userStatisticsService = new UserStatisticsService(userStatisticsRepository, rankingListRepository, userRepository);
  }

  @Test
  public void todoFindName() {
    userStatisticsService.pullRequestWasClosed(pullRequest, CLOSED_AT);
  }
}

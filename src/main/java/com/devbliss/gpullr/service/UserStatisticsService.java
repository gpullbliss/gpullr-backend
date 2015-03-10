package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserStatistics;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.repository.UserStatisticsRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business logic handling {@link UserStatistics} objects. 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class UserStatisticsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserStatisticsService.class);

  private final UserStatisticsRepository userStatisticsRepository;

  private final RankingService rankingService;

  private final UserRepository userRepository;

  @Autowired
  public UserStatisticsService(
      UserStatisticsRepository userStatisticsRepository,
      RankingService rankingService,
      UserRepository userRepository) {
    this.userStatisticsRepository = userStatisticsRepository;
    this.rankingService = rankingService;
    this.userRepository = userRepository;
  }

  public void pullRequestWasClosed(PullRequest pullRequest, ZonedDateTime closedAt) {
    saveClosedPullRequestStatistic(pullRequest, closedAt);
    rankingService.recalculateRankings();
  }

  private void saveClosedPullRequestStatistic(PullRequest pullRequest, ZonedDateTime closedAt) {
    if (pullRequest.assignee == null) {
      LOGGER.warn("Pullrequest " + pullRequest.title + " / " + pullRequest.url + " was closed without assignee.");
      return;
    }

    Optional<UserStatistics> existingUserStatistics = userStatisticsRepository.findByUserId(pullRequest.assignee.id);
    UserStatistics userStatistics;

    if (existingUserStatistics.isPresent()) {
      userStatistics = existingUserStatistics.get();
    } else {
      User assignee = userRepository.findById(pullRequest.assignee.id).orElseThrow(
          () -> new NotFoundException("Assignee not found in database: " + pullRequest.assignee));
      userStatistics = new UserStatistics(assignee);
    }

    userStatistics.userHasClosedPullRequest(pullRequest, closedAt);
    userStatisticsRepository.save(userStatistics);
  }
}

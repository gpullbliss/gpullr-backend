package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.UserStatistics;
import com.devbliss.gpullr.repository.UserStatisticsRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

  @Autowired
  public UserStatisticsService(UserStatisticsRepository userStatisticsRepository, RankingService rankingService) {
    this.userStatisticsRepository = userStatisticsRepository;
    this.rankingService = rankingService;
  }

  public void pullRequestWasClosed(PullRequest pullRequest) {
    saveClosedPullRequestStatistic(pullRequest);
    rankingService.replace(calculateRankings());
  }

  private void saveClosedPullRequestStatistic(PullRequest pullRequest) {
    if (pullRequest.assignee == null) {
      LOGGER.warn("Pullrequest " + pullRequest.title + " / " + pullRequest.url + " was closed without assignee.");
      return;
    }

    Optional<UserStatistics> existingUserStatistics = userStatisticsRepository.findByUser(pullRequest.assignee);
    UserStatistics userStatistics;

    if (existingUserStatistics.isPresent()) {
      userStatistics = existingUserStatistics.get();
    } else {
      userStatistics = new UserStatistics(pullRequest.assignee);
    }

    userStatistics.userHasClosedPullRequest(pullRequest, ZonedDateTime.now());
    userStatisticsRepository.save(userStatistics);
  }

  private List<Ranking> calculateRankings() {

    List<UserStatistics> userStatistics = userStatisticsRepository.findAll();
    List<Ranking> allRankings = new ArrayList<>();

    for (RankingScope scope : RankingScope.values()) {
      List<Ranking> rankingsForScope = userStatistics
        .stream()
        .map(us -> us.getNumberOfClosedPullRequests(scope))
        .sorted((r1, r2) -> r1.numberOfMergedPullRequests.compareTo(r2.numberOfMergedPullRequests))
        .collect(Collectors.toList());

      for (int rank = 0; rank < allRankings.size(); rank++) {
        allRankings.get(rank).rank = rank + 1;
      }

      allRankings.addAll(rankingsForScope);
    }

    return allRankings;
  }
}

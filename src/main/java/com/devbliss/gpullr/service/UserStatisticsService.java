package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserStatistics;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.repository.UserStatisticsRepository;
import java.time.ZonedDateTime;
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

  private final RankingListRepository rankingListRepository;

  private final UserRepository userRepository;

  @Autowired
  public UserStatisticsService(
      UserStatisticsRepository userStatisticsRepository,
      RankingListRepository rankingListRepository,
      UserRepository userRepository) {
    this.userStatisticsRepository = userStatisticsRepository;
    this.rankingListRepository = rankingListRepository;
    this.userRepository = userRepository;
  }

  public void pullRequestWasClosed(PullRequest pullRequest, ZonedDateTime closedAt) {
    saveClosedPullRequestStatistic(pullRequest, closedAt);
    recalculateRankings();
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

  private void recalculateRankings() {
    ZonedDateTime now = ZonedDateTime.now();

    for (RankingScope rankingScope : RankingScope.values()) {
      rankingListRepository.save(new RankingList(
          calculateRankingsForScope(rankingScope),
          now,
          rankingScope));
      deleteRankingListsOlderThan(now, rankingScope);
    }
  }

  private void deleteRankingListsOlderThan(ZonedDateTime calculationDate, RankingScope rankingScope) {
    List<RankingList> rankingsToDelete = rankingListRepository.findByCalculationDateBeforeAndRankingScope(
        calculationDate, rankingScope);
    rankingListRepository.delete(rankingsToDelete);
  }

  private List<Ranking> calculateRankingsForScope(RankingScope rankingScope) {

    List<UserStatistics> userStatistics = userStatisticsRepository.findAll();

    List<Ranking> rankingsForScope = userStatistics
      .stream()
      .map(us -> us.getNumberOfClosedPullRequests(rankingScope))
      .sorted((r1, r2) -> r1.numberOfMergedPullRequests.compareTo(r2.numberOfMergedPullRequests))
      .collect(Collectors.toList());

    for (int rank = 0; rank < rankingsForScope.size(); rank++) {
      rankingsForScope.get(rank).rank = rank + 1;
    }

    return rankingsForScope;
  }
}

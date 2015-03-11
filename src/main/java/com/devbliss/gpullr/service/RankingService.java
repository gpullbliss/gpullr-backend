package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserHasClosedPullRequest;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserHasClosedPullRequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link RankingList} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class RankingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RankingService.class);

  private final RankingListRepository rankingListRepository;

  private final UserHasClosedPullRequestRepository userHasClosedPullRequestRepository;

  private final UserRepository userRepository;

  @Autowired
  public RankingService(
      RankingListRepository rankingListRepository,
      UserHasClosedPullRequestRepository userHasClosedPullRequestRepository,
      UserRepository userRepository) {
    this.rankingListRepository = rankingListRepository;
    this.userHasClosedPullRequestRepository = userHasClosedPullRequestRepository;
    this.userRepository = userRepository;
  }

  public Optional<RankingList> findAllWithRankingScope(RankingScope rankingScope) {
    List<RankingList> rankingLists = rankingListRepository.findByRankingScopeOrderByCalculationDateDesc(rankingScope);

    if (!rankingLists.isEmpty()) {
      RankingList rankingList = rankingLists.get(0);
      LOGGER.debug("Returning rankings calculated at " + rankingList.calculationDate.toString() + ": ");
      LOGGER.debug("" + rankingList.getRankings());
      return Optional.of(rankingList);
    }

    LOGGER.debug("No ranking list found for scope " + rankingScope + " - no rankings found.");
    return Optional.empty();
  }

  public void recalculateRankings() {
    ZonedDateTime now = ZonedDateTime.now();

    for (RankingScope rankingScope : RankingScope.values()) {
      rankingListRepository.save(new RankingList(
          calculateRankingsForScope(rankingScope),
          now,
          rankingScope));
      deleteRankingListsOlderThan(now, rankingScope);
    }
  }

  public void userHasClosedPullRequest(PullRequest closedPullRequest, ZonedDateTime closeDate) {

    if (closedPullRequest.assignee == null) {
      LOGGER.warn("Cannot update statistics for closed pull request " + closedPullRequest.url + ": assignee is null.");
      return;
    }

    Optional<User> closer = userRepository.findById(closedPullRequest.assignee.id);

    if (!closer.isPresent()) {
      LOGGER.warn("Cannot update statistics for closed pull request " + closedPullRequest.url + ": assignee with id "
          + closedPullRequest.assignee.id + " not found in our database.");
      return;
    }

    UserHasClosedPullRequest userHasClosedPullRequest = new UserHasClosedPullRequest(closer.get(), closeDate,
        closedPullRequest.url);
    userHasClosedPullRequestRepository.save(userHasClosedPullRequest);
  }

  private void deleteRankingListsOlderThan(ZonedDateTime calculationDate, RankingScope rankingScope) {
    List<RankingList> rankingsToDelete = rankingListRepository.findByCalculationDateBeforeAndRankingScope(
        calculationDate, rankingScope);
    rankingListRepository.delete(rankingsToDelete);
  }

  private List<Ranking> calculateRankingsForScope(RankingScope rankingScope) {

    List<User> userStatistics = userRepository.findAll();

    List<Ranking> rankingsForScope = userStatistics
      .stream()
      .map(u -> getRanking(u, rankingScope))
      .sorted((r1, r2) -> r2.closedCount.compareTo(r1.closedCount))
      .collect(Collectors.toList());
    return rankingsForScope;
  }

  private Ranking getRanking(User user, RankingScope rankingScope) {
    long numberOfMergedPullRequests;

    if (rankingScope.daysInPast.isPresent()) {
      ZonedDateTime boarder = ZonedDateTime.now().minusDays(rankingScope.daysInPast.get());
      numberOfMergedPullRequests = userHasClosedPullRequestRepository.findByUser(user)
        .stream()
        .filter(uhcp -> !uhcp.closeDate.isBefore(boarder))
        .count();
    } else {
      numberOfMergedPullRequests = Long.valueOf(userHasClosedPullRequestRepository.findByUser(user).size());
    }

    return new Ranking(user.username, numberOfMergedPullRequests);
  }
}

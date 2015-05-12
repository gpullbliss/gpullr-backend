package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingData;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link RankingList} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class RankingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RankingService.class);

  private final RankingListRepository rankingListRepository;

  private final PullRequestRepository pullRequestRepository;

  private final UserRepository userRepository;

  @Autowired
  public RankingService(
      RankingListRepository rankingListRepository,
      PullRequestRepository pullRequestRepository,
      UserRepository userRepository) {
    this.rankingListRepository = rankingListRepository;
    this.pullRequestRepository = pullRequestRepository;
    this.userRepository = userRepository;
  }

  public Optional<RankingList> findAllWithRankingScope(RankingScope rankingScope) {
    List<RankingList> rankingLists = rankingListRepository.findByRankingScopeOrderByCalculationDateDesc(rankingScope);

    if (!rankingLists.isEmpty()) {
      RankingList rankingList = rankingLists.get(0);
      LOGGER.debug("Returning rankings calculated at " + rankingList.calculationDate.toString());
      return Optional.of(rankingList);
    }

    LOGGER.debug("No ranking list found for scope " + rankingScope + " - no rankings found.");
    return Optional.empty();
  }

  public List<RankingList> findAll() {
    return rankingListRepository.findAll();
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

  private void deleteRankingListsOlderThan(ZonedDateTime calculationDate, RankingScope rankingScope) {
    List<RankingList> rankingsToDelete = rankingListRepository.findByCalculationDateBeforeAndRankingScope(
        calculationDate, rankingScope);
    rankingListRepository.delete(rankingsToDelete);
  }

  private List<Ranking> calculateRankingsForScope(RankingScope rankingScope) {
    List<Ranking> rankings = userRepository
      .findByCanLoginIsTrue()
      .stream()
      .map(u -> getRanking(u, rankingScope))
      .filter(or -> or.isPresent())
      .map(or -> or.get())
      .sorted((r1, r2) -> r2.getScore().compareTo(r1.getScore()))
      .collect(Collectors.toList());

    int count = 0;
    double previousScore = -1d;
    for (Ranking r : rankings) {
      if (r.getScore() != previousScore) {
        count++;
      }
      r.setRank(count);
      previousScore = r.getScore();
    }

    return rankings;
  }

  private Optional<Ranking> getRanking(User user, RankingScope rankingScope) {
    Predicate<PullRequest> filter;

    if (rankingScope.daysInPast.isPresent()) {
      ZonedDateTime border = ZonedDateTime.now().minusDays(rankingScope.daysInPast.get());
      filter = pr -> !pr.closedAt.isBefore(border);
    } else {
      filter = pr -> true;
    }

    List<PullRequest> pullRequests = pullRequestRepository.findByAssigneeAndState(user, State.CLOSED)
      .stream()
      .filter(pr -> !pr.assignee.id.equals(pr.author.id))
      .filter(filter).collect(Collectors.toList());

    if (pullRequests.isEmpty()) {
      return Optional.empty();
    } else {
      RankingData rankingData = pullrequestsToRankingData(pullRequests);
      return Optional.of(new Ranking(rankingData, user));
    }
  }

  private RankingData pullrequestsToRankingData(List<PullRequest> pullRequests) {
    RankingData rankingData = new RankingData();
    pullRequests
      .stream()
      .peek(p -> rankingData.addToSumOfLinesAdded(p.linesAdded))
      .peek(p -> rankingData.addToSumOfLinesRemoved(p.linesRemoved))
      .peek(p -> rankingData.addToSumOfFilesChanged(p.filesChanged))
      .peek(p -> rankingData.addToClosedCount(1))
      .forEach(p -> rankingData.addToSumOfComments(p.numberOfComments));
    return rankingData;
  }
}

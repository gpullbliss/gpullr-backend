package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RankingListRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

  /**
   * Compares user by full name if set or username otherwise.
   */
  private final Comparator<User> userByFullnameAndUsernameComparator = new Comparator<User>() {

    @Override
    public int compare(User u1, User u2) {
      String name1 = u1.fullName != null && !u1.fullName.isEmpty() ? u1.fullName : u1.username;
      String name2 = u2.fullName != null && !u2.fullName.isEmpty() ? u2.fullName : u2.username;
      return name1.toLowerCase().compareTo(name2.toLowerCase());
    }
  };

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
      rankingList.getRankings().forEach(r -> r.users.sort(userByFullnameAndUsernameComparator));
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
    List<User> users = userRepository.findByCanLoginIsTrue();
    Map<Double, Ranking> numberOfMergedPullRequestsToRankings = new HashMap<>();
    users.forEach(u -> addRankingOfUserToMap(numberOfMergedPullRequestsToRankings, u, rankingScope));
    List<Ranking> rankings = numberOfMergedPullRequestsToRankings
      .keySet()
      .stream()
      .filter(n -> n > 0)
      .sorted((n1, n2) -> n2.compareTo(n1))
      .map(n -> numberOfMergedPullRequestsToRankings.get(n))
      .collect(Collectors.toList());
    IntStream.range(0, rankings.size()).forEach(i -> rankings.get(i).rank = i + 1);
    return rankings;
  }

  private void addRankingOfUserToMap(Map<Double, Ranking> rankings, User user, RankingScope rankingScope) {
    double sumOfScores = getRanking(user, rankingScope);
    Ranking ranking = rankings.get(sumOfScores);

    if (ranking == null) {
      ranking = new Ranking();
      ranking.sumOfScores = sumOfScores;
      rankings.put(sumOfScores, ranking);
    }

    ranking.users.add(user);
  }


  private Double getRanking(User user, RankingScope rankingScope) {

    Predicate<PullRequest> filter;

    if (rankingScope.daysInPast.isPresent()) {
      ZonedDateTime border = ZonedDateTime.now().minusDays(rankingScope.daysInPast.get());
      filter = pr -> !pr.closedAt.isBefore(border);
    } else {
      filter = pr -> true;
    }

    return pullRequestRepository.findByAssigneeAndState(user, State.CLOSED)
        .stream()
        .filter(pr -> !pr.assignee.id.equals(pr.author.id))
        .filter(filter)
        .map(pr -> pr.calculateScore())
        .reduce((sc0, sc1) -> sc0+sc1).orElse(0d);
  }

}

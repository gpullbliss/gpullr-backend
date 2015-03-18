package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business facade for persisting and retrieving {@link PullRequest} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */

@Service
public class PullRequestService {

  private final Comparator<PullRequest> latestFirstComparator = new Comparator<PullRequest>() {

    @Override
    public int compare(PullRequest p1, PullRequest p2) {
      return p2.createdAt.compareTo(p1.createdAt);
    }
  };

  private final Comparator<PullRequest> oldestFirstComparator = new Comparator<PullRequest>() {

    @Override
    public int compare(PullRequest p1, PullRequest p2) {
      return p1.createdAt.compareTo(p2.createdAt);
    }
  };

  private Logger LOGGER = LoggerFactory.getLogger(PullRequestService.class);

  private final PullRequestRepository pullRequestRepository;

  private final UserRepository userRepository;

  private final GithubApi githubApi;

  private final UserService userService;

  @Autowired
  public PullRequestService(
      PullRequestRepository pullRequestRepository,
      UserRepository userRepository,
      GithubApi githubApi,
      UserService userService) {
    this.pullRequestRepository = pullRequestRepository;
    this.userRepository = userRepository;
    this.githubApi = githubApi;
    this.userService = userService;
  }

  public List<PullRequest> findAll() {
    return pullRequestRepository
      .findAll()
      .stream()
      .sorted((p1, p2) -> p1.createdAt.compareTo(p2.createdAt))
      .collect(Collectors.toList());
  }

  /**
   * Finds all open pull requests sorted according to user settings, 
   * defaulting to sorting by creation date, latest first.
   *
   * @return possibly empty list of pull requests
   */
  public List<PullRequest> findAllOpen() {
    List<PullRequest> pullRequests = pullRequestRepository
      .findAllByState(PullRequest.State.OPEN)
      .stream()
      .sorted(getPullRequestSortComparator(userService.getCurrentUserIfLoggedIn()))
      .collect(Collectors.toList());

    return pullRequests;
  }

  /**
   * Finds all closed pull requests sorted by closed date, earliest first.
   *
   * @return possibly empty list of pull requests
   */
  public List<PullRequest> findAllClosed() {
    List<PullRequest> pullRequests = pullRequestRepository
      .findAllByState(PullRequest.State.CLOSED)
      .stream()
      .sorted((p1, p2) -> p1.closedAt.compareTo(p2.closedAt))
      .collect(Collectors.toList());

    return pullRequests;
  }

  private Comparator<PullRequest> getPullRequestSortComparator(Optional<User> currentUser) {
    User user = currentUser.orElse(null);
    UserSettings userSettings = user != null ? user.userSettings : null;

    if (userSettings == null || userSettings.defaultPullRequestListOrdering == UserSettings.OrderOption.DESC) {
      return latestFirstComparator;
    } else {
      return oldestFirstComparator;
    }
  }

  public Optional<PullRequest> findById(Integer id) {
    return pullRequestRepository.findById(id);
  }

  public boolean exists(Integer id) {
    return pullRequestRepository.exists(id);
  }

  public void assignPullRequest(User user, Integer pullRequestId) {
    PullRequest pullRequest = pullRequestRepository
      .findById(pullRequestId)
      .orElseThrow(() -> new NotFoundException("No pullRequest found with id " + pullRequestId));

    if (isUserUnknown(user)) {
      throw new NotFoundException("Cannot assign unknown user " + user.username + " to a pullRequest.");
    }

    githubApi.assignUserToPullRequest(user, pullRequest);
    pullRequest.assignedAt = ZonedDateTime.now();
    pullRequest.assignee = user;
    pullRequestRepository.save(pullRequest);
  }

  public void insertOrUpdate(PullRequest pullRequest) {
    if (isUserUnknown(pullRequest.author)) {
      userRepository.save(pullRequest.author);
    }

    Optional<PullRequest> existing = pullRequestRepository.findById(pullRequest.id);

    if (existing.isPresent()) {
      pullRequest = syncPullRequestData(existing.get(), pullRequest);
    } else {
      pullRequest = ensureClosedAtIfClosed(pullRequest);
    }

    pullRequestRepository.save(pullRequest);
  }

  private boolean isUserUnknown(User user) {
    return userRepository.findOne(user.id) == null;
  }

  private PullRequest syncPullRequestData(PullRequest existing, PullRequest update) {
    LOGGER.debug("Updating PR data from update for PR " + existing);

    if (update.assignee == null) {
      update.assignee = existing.assignee;
      LOGGER
        .debug(String.format("kept existing assignee from database for pullrequest %s", existing.assignee, existing));
    }

    if (update.assignedAt == null) {
      update.assignedAt = existing.assignedAt;
      LOGGER
        .debug(String.format("kept existing assignedAt '%s' from database for pullrequest %s", existing.assignedAt,
            existing));
    }

    if (update.closedAt == null) {
      update.closedAt = existing.closedAt;
      LOGGER.debug(String.format("kept existing closedAt '%s' from database for pullrequest %s", existing.closedAt,
          existing));
    }

    if (update.state == null) {
      update.state = existing.state;
    }

    if (update.repo == null) {
      update.repo = existing.repo;
    }

    update = ensureClosedAtIfClosed(update);
    return update;
  }

  private PullRequest ensureClosedAtIfClosed(PullRequest pullRequest) {
    if (pullRequest.state == State.CLOSED && pullRequest.closedAt == null) {
      pullRequest.closedAt = ZonedDateTime.now();
      LOGGER.debug("Set current date as fallback closedAt for pullrequest " + pullRequest);
    }

    return pullRequest;
  }
}

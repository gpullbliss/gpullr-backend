package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business facade for persisting and retrieving {@link PullRequest} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */

@Service
public class PullRequestService {

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

  private List<PullRequest> orderPullRequestsByUserPreference(List<PullRequest> pullRequests) {
    UserSettings userSettings = userService.whoAmI().userSettings;
    if (userSettings != null && userSettings.defaultPullRequestListOrdering != null) {
      if (userSettings.defaultPullRequestListOrdering == UserSettings.OrderOption.ASC) {
        Collections.reverse(pullRequests);
      }
    }

    return pullRequests;
  }

  /**
   * Finds all open pull requests sorted by creation date, latest first.
   *
   * @return possibly empty list of pull requests
   */
  public List<PullRequest> findAllOpen() {
    List<PullRequest> pullRequests = pullRequestRepository
      .findAllByState(PullRequest.State.OPEN)
      .stream()
      .sorted((p1, p2) -> p2.createdAt.compareTo(p1.createdAt))
      .collect(Collectors.toList());

    return orderPullRequestsByUserPreference(pullRequests);
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

    // assignee is null in GitHub response => save assignee if assigned via gpullr:
    pullRequestRepository.findById(pullRequest.id).ifPresent(existing -> {
      if (pullRequest.assignee == null) {
        pullRequest.assignee = existing.assignee;
        pullRequest.assignedAt = existing.assignedAt;
      } else if (pullRequest.assignee.id.intValue() != existing.assignee.id.intValue()) {
        pullRequest.assignedAt = ZonedDateTime.now();
      }
    });
    pullRequestRepository.save(pullRequest);
  }

  private boolean isUserUnknown(User user) {
    return userRepository.findOne(user.id) == null;
  }
}

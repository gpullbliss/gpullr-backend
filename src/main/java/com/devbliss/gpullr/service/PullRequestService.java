package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
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

  @Autowired
  public PullRequestService(
      PullRequestRepository pullRequestRepository,
      UserRepository userRepository,
      GithubApi githubApi) {
    this.pullRequestRepository = pullRequestRepository;
    this.userRepository = userRepository;
    this.githubApi = githubApi;
  }

  public List<PullRequest> findAll() {

    return pullRequestRepository
      .findAll()
      .stream()
      .sorted((p1, p2) -> p1.createdAt.compareTo(p2.createdAt))
      .collect(Collectors.toList());
  }

  /**
   * Finds all open pull requests sorted by creation date, latest first.
   * 
   * @return possibly empty list of pull requests 
   */
  public List<PullRequest> findAllOpen() {
    return pullRequestRepository
      .findAllByState(PullRequest.State.OPEN)
      .stream()
      .sorted((p1, p2) -> p2.createdAt.compareTo(p1.createdAt))
      .collect(Collectors.toList());
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
    pullRequest.assignee = user;
    pullRequestRepository.save(pullRequest);
  }

  public void insertOrUpdate(PullRequest pullRequest) {
    if (isUserUnknown(pullRequest.owner)) {
      userRepository.save(pullRequest.owner);
    }
    pullRequestRepository.save(pullRequest);
  }

  private boolean isUserUnknown(User user) {
    return userRepository.findOne(user.id) == null;
  }
}
package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.BuildStatus;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  private static final String NO_SUCH_REPO_MESSAGE = "No pullRequest found with id ";

  private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestService.class);

  private final PullRequestRepository pullRequestRepository;

  private final UserRepository userRepository;

  private final GithubApi githubApi;

  private final UserService userService;

  private final RepoRepository repoRepository;

  private final UserNotificationService notificationService;

  @Autowired
  public PullRequestService(
      PullRequestRepository pullRequestRepository,
      UserRepository userRepository,
      GithubApi githubApi,
      UserService userService,
      RepoRepository repoRepository,
      UserNotificationService notificationService) {
    this.pullRequestRepository = pullRequestRepository;
    this.userRepository = userRepository;
    this.githubApi = githubApi;
    this.userService = userService;
    this.repoRepository = repoRepository;
    this.notificationService = notificationService;

  }

  public List<PullRequest> findAll() {
    return pullRequestRepository
      .findAll()
      .stream()
      .sorted((p1, p2) -> p1.createdAt.compareTo(p2.createdAt))
      .collect(Collectors.toList());
  }

  /**
   * Finds all open pull requests sorted by creation date, latest first (default).<br /><br />
   * <p>
   * If there is a session:
   * <ul>
   * <li>user order options will be used for sorting</li>
   * <li>user repo blacklist will be applied</li>
   * </ul>
   *
   * @return possibly empty list of pull requests
   */
  public List<PullRequest> findAllOpen(boolean specificForCurrentUser) {
    List<PullRequest> prs = pullRequestRepository.findAllByState(State.OPEN);

    if (specificForCurrentUser) {
      Optional<User> user = userService.getCurrentUserIfLoggedIn();

      if (user.isPresent()) {
        UserSettings userSettings = user.get().userSettings;
        if (hasBlacklistedRepos(user.get())) {
          prs = prs
            .stream()
            .filter(pr -> !userSettings.repoBlackList.contains(pr.repo.id))
            .collect(Collectors.toList());
        }
      }
    }

    return prs;
  }

  public List<PullRequest> findAllOpenFiltered(String... repoIdsOrNames) {
    List<Repo> repos = Stream.of(repoIdsOrNames)
      .map(ion -> findRepoByIdOrName(ion))
      .collect(Collectors.toList());

    List<PullRequest> openPullRequests = findAllOpen(true);

    return openPullRequests
      .stream()
      .filter(pr -> repos.contains(pr.repo))
      .collect(Collectors.toList());
  }

  private boolean hasBlacklistedRepos(User user) {
    UserSettings userSettings = user.userSettings;
    if (userSettings == null) {
      return false;
    }
    if (userSettings.repoBlackList == null) {
      return false;
    }
    return userSettings.repoBlackList.size() > 0;
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
  
  public boolean shouldCommitsBeFetchedForPullRequest(PullRequest pullRequest) {
    return pullRequest.assignee != null;
  }

  private Repo findRepoByIdOrName(String idOrName) {
    Optional<Repo> repo;

    if (idOrName.matches("\\d+")) {
      repo = repoRepository.findById(Integer.valueOf(idOrName));

      if (!repo.isPresent()) {
        repo = repoRepository.findByName(idOrName);
      }
    } else {
      repo = repoRepository.findByName(idOrName);
    }

    return repo.orElseThrow(() -> new NotFoundException("No repo found with id or name '" + idOrName + "'."));
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
      .orElseThrow(() -> new NotFoundException(NO_SUCH_REPO_MESSAGE + pullRequestId));

    if (isUserUnknown(user)) {
      throw new NotFoundException("Cannot assign unknown user " + user.username + " to a pullRequest.");
    }

    githubApi.assignUserToPullRequest(user, pullRequest);
    pullRequest.assignedAt = ZonedDateTime.now();
    pullRequest.assignee = user;
    pullRequestRepository.save(pullRequest);
  }

  public void unassignPullRequest(User user, Integer pullRequestId) {
    PullRequest pullRequest = pullRequestRepository
      .findById(pullRequestId)
      .orElseThrow(() -> new NotFoundException(NO_SUCH_REPO_MESSAGE + pullRequestId));

    if (isUserUnknown(user)) {
      throw new NotFoundException("Cannot unassign unknown user " + user.username + " from a pullRequest.");
    }

    githubApi.unassignUserFromPullRequest(user, pullRequest);

    pullRequest.assignedAt = null;
    pullRequest.assignee = null;
    pullRequestRepository.save(pullRequest);
  }

  public void insertOrUpdate(PullRequest pullRequest) {
    if (isUserUnknown(pullRequest.author)) {
      userRepository.save(pullRequest.author);
    }

    if (isUserUnknown(pullRequest.assignee)) {
      userRepository.save(pullRequest.assignee);
    }

    Optional<PullRequest> existing = pullRequestRepository.findById(pullRequest.id);

    if (existing.isPresent()) {
      pullRequest = syncPullRequestData(existing.get(), pullRequest);
    } else {
      pullRequest = ensureClosedAtIfClosed(pullRequest);
      pullRequest = ensureAssignedAtIfAssigned(pullRequest);
    }

    pullRequestRepository.save(pullRequest);

    if (pullRequest.state == State.CLOSED) {
      notificationService.createClosedPullRequestNotification(pullRequest);
    }
  }

  public void saveBuildstatus(int pullrequestId, BuildStatus buildStatus) {
    PullRequest pullRequest = pullRequestRepository
      .findById(pullrequestId)
      .orElseThrow(
          () -> new NotFoundException("Cannot save build status: no pull request found with id " + pullrequestId));
    pullRequest.buildStatus = buildStatus;
    pullRequestRepository.save(pullRequest);
  }

  private boolean isUserUnknown(User user) {
    return user != null && !userRepository.exists(user.id);
  }

  private PullRequest syncPullRequestData(PullRequest existing, PullRequest update) {
    LOGGER.debug("Updating PR data from update for PR " + existing);

    if (update.assignee == null) {
      update.assignee = existing.assignee;
      LOGGER.debug("kept existing assignee {} from database for pull request {}", existing.assignee, existing);
    }

    if (update.assignedAt == null) {
      update.assignedAt = existing.assignedAt;
      LOGGER.debug("kept existing assignedAt '{}' from database for pull request {}", existing.assignedAt, existing);
    }

    if (update.closedAt == null) {
      update.closedAt = existing.closedAt;
      LOGGER.debug("kept existing closedAt '{}' from database for pull request {}", existing.closedAt, existing);
    }

    if (update.state == null) {
      update.state = existing.state;
    }

    if (update.repo == null) {
      update.repo = existing.repo;
    }

    update = ensureClosedAtIfClosed(update);
    update = ensureAssignedAtIfAssigned(update);
    return update;
  }

  private PullRequest ensureClosedAtIfClosed(PullRequest pullRequest) {
    if (pullRequest.state == State.CLOSED && pullRequest.closedAt == null) {
      pullRequest.closedAt = ZonedDateTime.now();
      LOGGER.debug("Set current date as fallback closedAt for pull request " + pullRequest);
    }

    return pullRequest;
  }

  private PullRequest ensureAssignedAtIfAssigned(PullRequest pullRequest) {
    if (pullRequest.assignee != null && pullRequest.assignedAt == null) {
      pullRequest.assignedAt = ZonedDateTime.now();
      LOGGER.debug("Set current date as fallback assignedAt for pull request " + pullRequest);
    }

    return pullRequest;
  }
}

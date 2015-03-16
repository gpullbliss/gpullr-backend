package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestService;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

/**
 * Watcher for assignee for a certain pull requests. 
 * Once started, it runs forever until {@link #pleaseStop()} has been called.
 * 
 * When running, it periodically fetches the details of its pull request from GitHub API. 
 * It uses the ETAG header in order not to waste the request quota.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class PullRequestAssigneeWatchThread extends Thread {

  private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestAssigneeWatchThread.class);

  public final int pullRequestId;

  private final TaskScheduler taskScheduler;

  private final GithubApi githubApi;

  private final PullRequestService pullRequestService;

  private boolean stopped = false;

  public PullRequestAssigneeWatchThread(
      int pullRequestId,
      TaskScheduler taskScheduler,
      GithubApi githubApi,
      PullRequestService pullRequestService) {
    this.pullRequestId = pullRequestId;
    this.taskScheduler = taskScheduler;
    this.githubApi = githubApi;
    this.pullRequestService = pullRequestService;
  }

  @Override
  public void run() {
    fetch(Optional.empty());
  }

  /**
   * Call this to stop the infinite loop of fetching. Will not cancel a request that is currently running but make
   * sure there is no follow up request.
   */
  public void pleaseStop() {
    stopped = true;
  }

  private void fetch(Optional<String> etagHeader) {
    pullRequestService
      .findById(pullRequestId)
      .ifPresent(pr -> handleResponse(pr, githubApi.fetchPullRequest(pr, etagHeader)));
  }

  private void handleResponse(PullRequest pullRequestFromDb, GithubPullrequestResponse resp) {
    resp.payload.ifPresent(pr -> handlePullRequest(pullRequestFromDb, pr));

    if (!stopped) {
      Date nextFetch = Date.from(resp.nextFetch);
      taskScheduler.schedule(() -> fetch(resp.etagHeader), nextFetch);
    }
  }

  private void handlePullRequest(PullRequest pullRequestFromDb, PullRequest fetchedPullRequest) {
    pullRequestService.findById(pullRequestId).ifPresent(p -> synchronizePullRequestData(p, fetchedPullRequest));
  }

  private void synchronizePullRequestData(PullRequest pullRequestFromDb, PullRequest fetchedPullRequest) {
    LOGGER.debug("synchronizing PR data in watch thread for PR " + pullRequestFromDb.url);

    if (fetchedPullRequest.assignee != null) {
      pullRequestFromDb.assignee = fetchedPullRequest.assignee;
      LOGGER.debug("stored assignee " + pullRequestFromDb.assignee.username + " for pullrequest " + pullRequestFromDb);
    }

    if (fetchedPullRequest.assignedAt != null) {
      pullRequestFromDb.assignedAt = fetchedPullRequest.assignedAt;
      LOGGER.debug("stored assignedAt " + pullRequestFromDb.assignedAt + " for pullrequest " + pullRequestFromDb);
    }

    if (fetchedPullRequest.closedAt != null) {
      pullRequestFromDb.closedAt = fetchedPullRequest.createdAt;
      LOGGER.debug("stored closedAt " + pullRequestFromDb.closedAt + " for pullrequest " + pullRequestFromDb);
    }

    pullRequestService.insertOrUpdate(pullRequestFromDb);
  }
}

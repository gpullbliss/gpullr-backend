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

  public final PullRequest pullRequest;

  private final TaskScheduler taskScheduler;

  private final GithubApi githubApi;

  private final PullRequestService pullRequestService;

  private boolean stopped = false;

  public PullRequestAssigneeWatchThread(
      PullRequest pullRequest,
      TaskScheduler taskScheduler,
      GithubApi githubApi,
      PullRequestService pullRequestService) {
    this.pullRequest = pullRequest;
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
    handleResponse(githubApi.fetchPullRequest(pullRequest, etagHeader));
  }

  private void handleResponse(GithubPullrequestResponse resp) {
    resp.payload.ifPresent(this::handlePullRequest);

    if (!stopped) {
      Date nextFetch = Date.from(resp.nextFetch);
      taskScheduler.schedule(() -> fetch(resp.etagHeader), nextFetch);
    }
  }

  private void handlePullRequest(PullRequest fetchedPullRequest) {
    if (fetchedPullRequest.assignee != null) {
      pullRequest.assignee = fetchedPullRequest.assignee;
      pullRequestService.insertOrUpdate(pullRequest);
      LOGGER.debug("stored assignee " + pullRequest.assignee.username + " for pullrequest " + pullRequest);
    }
  }
}

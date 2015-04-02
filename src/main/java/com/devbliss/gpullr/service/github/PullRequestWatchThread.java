package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.service.PullRequestService;
import java.util.Date;
import java.util.Optional;
import org.springframework.scheduling.TaskScheduler;

/**
 * Watcher for details for a certain pull requests. 
 * Once started, it runs forever until {@link #pleaseStop()} has been called.
 * Adds anything which is not delivered in the regular pull request event payload.
 * 
 * When running, it periodically fetches the details of its pull request from GitHub API. 
 * It uses the ETAG header in order not to waste the request quota.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class PullRequestWatchThread extends Thread {

  public final int pullRequestId;

  private final TaskScheduler taskScheduler;

  private final GithubApi githubApi;

  private final PullRequestService pullRequestService;

  private boolean stopped = false;

  public PullRequestWatchThread(
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
      .ifPresent(pr -> {
        handleResponse(githubApi.fetchPullRequest(pr, etagHeader));
        handleResponse(githubApi.fetchCiStatus(pr, etagHeader));
      });
  }

  private void handleResponse(GithubPullRequestResponse resp) {
    resp.payload.ifPresent(pullRequestService::insertOrUpdate);

    if (!stopped) {
      Date nextFetch = Date.from(resp.nextFetch);
      taskScheduler.schedule(() -> fetch(resp.etagHeader), nextFetch);
    }
  }

  private void handleResponse(GithubPullRequestBuildStatusResponse resp) {
    if (resp.payload.size() > 0) {
      pullRequestService.saveBuildstatus(pullRequestId, resp.payload.get(0));
    }
  }
}

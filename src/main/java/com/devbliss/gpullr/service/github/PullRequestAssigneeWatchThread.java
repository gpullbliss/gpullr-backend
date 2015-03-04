package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestService;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

public class PullRequestAssigneeWatchThread extends Thread {

  private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestAssigneeWatchThread.class);

  public final PullRequest pullRequest;

  private final TaskScheduler taskScheduler;

  private final GithubApi githubApi;

  private final PullRequestService pullRequestService;

  private boolean stopped = false;

  public PullRequestAssigneeWatchThread(PullRequest pullRequest, TaskScheduler taskScheduler, GithubApi githubApi, PullRequestService pullRequestService) {
    this.pullRequest = pullRequest;
    this.taskScheduler = taskScheduler;
    this.githubApi = githubApi;
    this.pullRequestService = pullRequestService;
  }

  @Override
  public void run() {
    fetch(Optional.empty());
  }

  private void fetch(Optional<String> etagHeader) {
    handleResponse(githubApi.fetchPullRequest(pullRequest, etagHeader));
  }
  
  private void handleResponse(GithubPullrequestResponse resp) {
    resp.payload.ifPresent(this::handlePullRequest);

    if (!stopped) {
      Date nextFetch = Date.from(Instant.now().plusSeconds(resp.nextRequestAfterSeconds));
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

  public void pleaseStop() {
    stopped = true;
  }
}

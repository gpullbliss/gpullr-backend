package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.util.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Handles pullRequest events fetched from GitHub and triggers the appropriate action in business layer.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class PullRequestEventHandler {

  @Log
  Logger logger;

  @Autowired
  private TaskScheduler taskScheduler;

  private final PullRequestService pullRequestService;

  private final PullRequestWatcher pullRequestWatcher;

  @Autowired
  public PullRequestEventHandler(
      PullRequestService pullRequestService,
      PullRequestWatcher pullRequestWatcher) {
    this.pullRequestService = pullRequestService;
    this.pullRequestWatcher = pullRequestWatcher;
  }

  public void handlePullRequestEvent(PullRequestEvent event) {
    PullRequest pullRequestFromEvent = event.pullRequest;
    Optional<PullRequest> pullRequestFromDb = pullRequestService.findById(pullRequestFromEvent.id);

    logger.debug("handling pr ev: " + pullRequestFromEvent.title + " / " + pullRequestFromEvent.state);

    if (event.action == PullRequestEvent.Action.OPENED) {
      pullRequestFromEvent.state = State.OPEN;
    } else if (event.action == PullRequestEvent.Action.CLOSED) {
      pullRequestFromEvent.state = State.CLOSED;
    } else if (event.action == PullRequestEvent.Action.REOPENED) {
      pullRequestFromEvent.state = State.OPEN;
    }

    if (pullRequestFromDb.isPresent() && pullRequestFromDb.get().updatedAt != null) {
      if (pullRequestFromEvent.updatedAt.isAfter(pullRequestFromDb.get().updatedAt)) {
        pullRequestService.insertOrUpdate(pullRequestFromEvent);
      }
    } else {
      pullRequestService.insertOrUpdate(pullRequestFromEvent);
    }

    // unfortunately, the assignee is not set in GitHub PR event if state is OPEN, so we have to
    // fetch it manually:
    if (pullRequestFromEvent.state == State.OPEN) {
      pullRequestWatcher.startWatching(pullRequestFromEvent);
    } else if (pullRequestFromEvent.state == State.CLOSED) {
      pullRequestWatcher.stopWatching(pullRequestFromEvent);
    }
  }
}

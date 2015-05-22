package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.util.Log;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles pullRequest events fetched from GitHub and triggers the appropriate action in business layer.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class PullRequestEventHandler {

  @Log
  Logger logger;

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

    if (event.action == PullRequestEvent.Action.OPENED) {
      pullRequestFromEvent.state = State.OPEN;
    } else if (event.action == PullRequestEvent.Action.CLOSED) {
      pullRequestFromEvent.state = State.CLOSED;
    } else if (event.action == PullRequestEvent.Action.REOPENED) {
      pullRequestFromEvent.state = State.OPEN;
    }

    logger.debug("handling pr ev: " + pullRequestFromEvent.title + " / " + pullRequestFromEvent.state);

    if (pullRequestFromDb.isPresent() && pullRequestFromDb.get().updatedAt != null) {
      if (pullRequestFromEvent.updatedAt.isAfter(pullRequestFromDb.get().updatedAt)) {
        savePullRequest(pullRequestFromEvent);
      }
    } else {
      savePullRequest(pullRequestFromEvent);
    }
  }

  private void savePullRequest(PullRequest pullRequest) {
    pullRequestService.insertOrUpdate(pullRequest);

    // unfortunately, the assignee is not set in GitHub PR event if state is OPEN, so we have to
    // fetch it manually:
    if (pullRequest.state == State.OPEN) {
      pullRequestWatcher.startWatching(pullRequest);
    } else if (pullRequest.state == State.CLOSED) {
      pullRequestWatcher.stopWatching(pullRequest);
    }
  }
}

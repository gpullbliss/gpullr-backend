package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.PullRequestEvent.Action;
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
 *
 */
@Service
public class PullRequestEventHandler {

  @Log
  Logger logger;

  private final PullRequestService pullRequestService;

  @Autowired
  public PullRequestEventHandler(PullRequestService pullRequestService) {
    this.pullRequestService = pullRequestService;
  }

  public void handlePullRequestEvent(PullRequestEvent event) {
    PullRequest pullRequestFromEvent = event.pullRequest;
    Optional<PullRequest> pullRequestFromDb = pullRequestService.findById(pullRequestFromEvent.id);

    if (event.action == Action.OPENED) {
      if (pullRequestFromDb.isPresent()) {
        pullRequestFromEvent.state = pullRequestFromDb.get().state;
      } else {
        pullRequestFromEvent.state = State.OPEN;
      }
    } else if (event.action == Action.CLOSED) {
      pullRequestFromEvent.state = State.CLOSED;
    } else if (event.action == Action.REOPENED) {
      pullRequestFromEvent.state = State.OPEN;
    }

    logger.debug("handling pr ev: " + pullRequestFromEvent.title + " / " + pullRequestFromEvent.state);

    if (pullRequestFromEvent.assignee != null) {
      logger.debug("3assigned_assignee_to_pullrequest: " + pullRequestFromEvent.title + " // "
          + pullRequestFromEvent.assignee + " /-/ " + pullRequestFromEvent.state);
    }
    pullRequestService.insertOrUpdate(pullRequestFromEvent);
  }
}

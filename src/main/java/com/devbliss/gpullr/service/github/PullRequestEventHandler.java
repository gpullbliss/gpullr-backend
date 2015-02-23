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
 * Handles pullrequest events fetched from GitHub and triggers the appropriate action in business layer. 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class PullRequestEventHandler {

  @Log
  Logger logger;

  private final PullRequestService pullrequestService;

  @Autowired
  public PullRequestEventHandler(PullRequestService pullrequestService) {
    this.pullrequestService = pullrequestService;
  }

  public void handlePullrequestEvent(PullRequestEvent event) {
    PullRequest pullrequestFromEvent = event.pullrequest;
    Optional<PullRequest> pullrequestFromDb = pullrequestService.findById(pullrequestFromEvent.id);

    if (event.action == Action.OPENED) {
      if (pullrequestFromDb.isPresent()) {
        pullrequestFromEvent.state = pullrequestFromDb.get().state;
      } else {
        pullrequestFromEvent.state = State.OPEN;
      }
    } else if (event.action == Action.CLOSED) {
      pullrequestFromEvent.state = State.CLOSED;
    } else if (event.action == Action.REOPENED) {
      pullrequestFromEvent.state = State.OPEN;
    }

    logger.debug("handling pr ev: " + event.pullrequest.title + " / " + event.pullrequest.state);
    pullrequestService.insertOrUpdate(pullrequestFromEvent);
  }
}

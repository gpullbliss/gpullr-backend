package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.Pullrequest.State;
import com.devbliss.gpullr.domain.PullrequestEvent;
import com.devbliss.gpullr.domain.PullrequestEvent.Action;
import com.devbliss.gpullr.service.PullrequestService;
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
public class PullrequestEventHandler {

  @Log
  Logger logger;

  private final PullrequestService pullrequestService;

  @Autowired
  public PullrequestEventHandler(PullrequestService pullrequestService) {
    this.pullrequestService = pullrequestService;
  }

  public void handlePullrequestEvent(PullrequestEvent event) {
    Pullrequest pullrequestFromEvent = event.pullrequest;
    Optional<Pullrequest> pullrequestFromDb = pullrequestService.findById(pullrequestFromEvent.id);

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

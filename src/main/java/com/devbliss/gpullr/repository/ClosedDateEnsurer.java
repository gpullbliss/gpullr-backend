package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.PullRequest.State;
import java.time.ZonedDateTime;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Ensures that all closed pull requests have a close date.
 * Might be necessary before merging new feature.
 * Should be removed soon.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class ClosedDateEnsurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClosedDateEnsurer.class);

  @Autowired
  private PullRequestRepository pullRequestRepository;

  @PostConstruct
  public void ensureCloseDate() {
    LOGGER.debug("Start ensuring close dates...");

    pullRequestRepository.findAllByState(State.CLOSED).forEach(pr -> {
      if (pr.closedAt == null) {
        pr.closedAt = ZonedDateTime.now();
        pullRequestRepository.save(pr);
        LOGGER.debug("Close date set for PR " + pr);
      }
    });
  }
}

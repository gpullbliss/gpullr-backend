package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequestEvent;
import java.util.List;
import java.util.Optional;

/**
 * Contains the relevant content of a response coming from GitHub when we request all events (for a certain repository).
 * <p>
 * That is: the events themselves, the time when we are allowed to poll the next time (in seconds from now) and
 * the ETAG header we should use for the next call to make sure we only receive events we haven't received already.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public class GithubEventsResponse extends AbstractGithubResponse<List<PullRequestEvent>> {

  public GithubEventsResponse(
      List<PullRequestEvent> pullRequestEvents,
      int nextRequestAfterSeconds,
      Optional<String> etagHeader) {
    super(pullRequestEvents, nextRequestAfterSeconds, etagHeader);
  }
}

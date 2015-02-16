package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullrequestEvent;
import java.util.List;
import java.util.Optional;

public class GithubEventsResponse {

  public final List<PullrequestEvent> pullrequestEvents;

  public final int nextRequestAfterSeconds;

  public final Optional<String> etagHeader;

  public GithubEventsResponse(
      List<PullrequestEvent> pullrequestEvents,
      int nextRequestAfterSeconds,
      Optional<String> etagHeader) {
    this.pullrequestEvents = pullrequestEvents;
    this.nextRequestAfterSeconds = nextRequestAfterSeconds;
    this.etagHeader = etagHeader;
  }
}

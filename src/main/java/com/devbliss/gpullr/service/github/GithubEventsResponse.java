package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullrequestEvent;
import java.util.List;

public class GithubEventsResponse {

  public final List<PullrequestEvent> pullrequestEvents;

  public final int nextRequestAfterSeconds;

  public final String etagHeader;

  public GithubEventsResponse(List<PullrequestEvent> pullrequestEvents, int nextRequestAfterSeconds, String etagHeader) {
    this.pullrequestEvents = pullrequestEvents;
    this.nextRequestAfterSeconds = nextRequestAfterSeconds;
    this.etagHeader = etagHeader;
  }
}

package com.devbliss.gpullr.domain;

import java.util.List;

public class GithubEventsResponse {
  
  public final List<? extends GithubEvent<?>> events;
  
  public final int nextRequestAfterSeconds;
  
  public final String etagHeader;
  
  public GithubEventsResponse(List<? extends GithubEvent<?>> events, int nextRequestAfterSeconds, String etagHeader) {
    this.events = events;
    this.nextRequestAfterSeconds = nextRequestAfterSeconds;
    this.etagHeader = etagHeader;
  }
}

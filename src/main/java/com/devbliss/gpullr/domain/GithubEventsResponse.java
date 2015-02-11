package com.devbliss.gpullr.domain;

import java.util.Date;
import java.util.List;

public class GithubEventsResponse {
  
  public final List<GithubEvent<?>> events;
  
  public final Date nextRequest;
  
  public GithubEventsResponse(List<GithubEvent<?>> events, Date nextRequest) {
    this.events = events;
    this.nextRequest = nextRequest;
  }
}

package com.devbliss.gpullr.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.context.ApplicationEvent;

/**
 * Created by abluem on 13/05/15.
 */
public class ApiRateLimitReachedEvent extends ApplicationEvent {

  public final ZonedDateTime resetTime;

  public ApiRateLimitReachedEvent(Object source, Instant resetTime) {
    super(source);
    this.resetTime = resetTime.atZone(ZoneId.systemDefault());
  }
}

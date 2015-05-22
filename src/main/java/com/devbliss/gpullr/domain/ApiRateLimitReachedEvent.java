package com.devbliss.gpullr.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.context.ApplicationEvent;

/**
 * Event to be thrown whenever the GitHub API has been queried to the limit. The reset time indicates the time when
 * the API is handing out data again.
 *
 * Created by alexander bluem on 13/05/15.
 */
public class ApiRateLimitReachedEvent extends ApplicationEvent {

  public final ZonedDateTime resetTime;

  public ApiRateLimitReachedEvent(Object source, Instant resetTime) {
    super(source);
    this.resetTime = resetTime.atZone(ZoneId.systemDefault());
  }
}

package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class UserHasClosedPullRequest {

  @NotNull
  @Column(nullable = false)
  public ZonedDateTime closeDate;

  public String pullRequestUrl;
  
  public UserHasClosedPullRequest() {}

  public UserHasClosedPullRequest(ZonedDateTime closeDate, String pullRequestUrl) {
    this.closeDate = closeDate;
    this.pullRequestUrl = pullRequestUrl;
  }
}

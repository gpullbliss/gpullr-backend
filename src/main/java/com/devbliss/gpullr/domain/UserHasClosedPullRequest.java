package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class UserHasClosedPullRequest {

  @NotNull
  @Column(nullable = false)
  public ZonedDateTime closeDate;

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  public PullRequest closedPullRequest;

  public UserHasClosedPullRequest() {}

  public UserHasClosedPullRequest(PullRequest closedPullRequest, ZonedDateTime closeDate) {
    this.closedPullRequest = closedPullRequest;
    this.closeDate = closeDate;
  }
}

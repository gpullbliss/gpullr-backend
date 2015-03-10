package com.devbliss.gpullr.domain;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

@Embeddable
public class Ranking {

  @NotNull
  public Integer rank;

  @NotBlank
  public String fullName;

  @NotNull
  public Long numberOfMergedPullRequests;

  public Ranking() {}

  public Ranking(String fullName, Long numberOfMergedPullRequests) {
    this.fullName = fullName;
    this.numberOfMergedPullRequests = numberOfMergedPullRequests;
  }
}

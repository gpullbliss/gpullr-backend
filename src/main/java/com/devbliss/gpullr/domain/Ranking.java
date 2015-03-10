package com.devbliss.gpullr.domain;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

@Embeddable
public class Ranking {

  @NotNull
  public Integer rank;

  @NotBlank
  public String username;

  @NotBlank
  public String avatarUrl;

  @NotNull
  public Long closedCount;

  public Ranking() {}

  public Ranking(String username, Long closedCount) {
    this.username = username;
    this.closedCount = closedCount;
  }

  @Override
  public String toString() {
    return "Ranking {rank=" + rank + ", username=" + username + ", closedCount=" + closedCount + "}";
  }
}

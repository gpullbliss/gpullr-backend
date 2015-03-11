package com.devbliss.gpullr.domain;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

@Embeddable
public class Ranking {

  @NotBlank
  public String username;

  @NotBlank
  public String avatarUrl;

  @NotNull
  public Long closedCount;

  public Ranking() {}

  public Ranking(String username, Long closedCount, String avatarUrl) {
    this.username = username;
    this.closedCount = closedCount;
    this.avatarUrl = avatarUrl;
  }

  @Override
  public String toString() {
    return "Ranking {username=" + username + ", closedCount=" + closedCount + "}";
  }
}

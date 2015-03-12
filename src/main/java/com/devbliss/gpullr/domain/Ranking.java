package com.devbliss.gpullr.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class Ranking {

  @ManyToOne(optional = false)
  public User user;

  @NotNull
  public Long closedCount;

  public Ranking() {}

  public Ranking(User user, Long closedCount) {
    this.user = user;
    this.closedCount = closedCount;
  }

  @Override
  public String toString() {
    return "Ranking {username=" + user.username + ", closedCount=" + closedCount + "}";
  }
}

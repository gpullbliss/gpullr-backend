package com.devbliss.gpullr.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

@Embeddable
public class Ranking {

  @ManyToMany
  public List<User> users;

  @NotNull
  public Long closedCount;
  
  public Integer rank;

  public Ranking() {
    users = new ArrayList<>();
  }

  public Ranking(Long closedCount, List<User> users) {
    this.users = users;
    this.closedCount = closedCount;
  }

  @Override
  public String toString() {
    return "Ranking {closedCount=" + closedCount + ", " + users + "}";
  }
}

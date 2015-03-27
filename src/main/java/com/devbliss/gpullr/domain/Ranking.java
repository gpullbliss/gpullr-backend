package com.devbliss.gpullr.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Ranking {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @ElementCollection
  @CollectionTable(name = "RANKING_USERS")
  public List<MinimalUser> users;

  @NotNull
  public Long closedCount;

  public Integer rank;

  public Ranking() {
    users = new ArrayList<>();
  }

  public Ranking(Long closedCount, List<MinimalUser> users) {
    this.users = users;
    this.closedCount = closedCount;
  }

  @Override
  public String toString() {
    return "Ranking {closedCount=" + closedCount + ", " + users + "}";
  }
}

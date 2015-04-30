package com.devbliss.gpullr.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Ranking {

  @NotNull
  public Double sumOfScores;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @ManyToMany(fetch = FetchType.EAGER)

  public List<User> users;

  public Integer rank;

  public Ranking() {
    users = new ArrayList<>();
  }

  public Ranking(double sumOfScores, List<User> users) {
    this.sumOfScores = sumOfScores;
    this.users = users;
  }

  @Override
  public String toString() {
    return "Ranking {sumOfScores=" + sumOfScores + ", " + users + "}";
  }
}

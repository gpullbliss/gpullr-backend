package com.devbliss.gpullr.domain;

import javax.persistence.Table;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="rankings")
public class Ranking {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @ManyToOne(fetch = FetchType.EAGER)
  public User user;

  @NotNull
  @Min(0L)
  public Integer rank;

  @Min(0L)
  public int sumOfLinesRemoved;

  @Min(0L)
  public int sumOfLinesAdded;

  @NotNull
  public Double sumOfScores;

  @NotNull
  public Integer closedCount;
  
  public Ranking() {
  }

  public Ranking(double sumOfScores, User user) {
    this.sumOfScores = sumOfScores;
    this.user = user;
  }

  @Override public String toString() {
    return "Ranking{" +
        "id=" + id +
        ", user=" + user +
        ", rank=" + rank +
        ", closedCount=" + closedCount +
        ", sumOfLinesRemoved=" + sumOfLinesRemoved +
        ", sumOfLinesAdded=" + sumOfLinesAdded +
        ", sumOfScores=" + sumOfScores +
        '}';
  }
}

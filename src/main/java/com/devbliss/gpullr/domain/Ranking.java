package com.devbliss.gpullr.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Ranking {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @NotNull
  public Integer rank;

  @NotBlank
  public String fullName;

  @NotNull
  public Long numberOfMergedPullRequests;

  @NotNull
  @Enumerated(EnumType.STRING)
  public RankingScope rankingScope;

  public Ranking() {}

  public Ranking(String fullName, Long numberOfMergedPullRequests, RankingScope rankingScope) {
    this.fullName = fullName;
    this.numberOfMergedPullRequests = numberOfMergedPullRequests;
    this.rankingScope = rankingScope;
  }
}

package com.devbliss.gpullr.domain;

import javax.persistence.EnumType;

import javax.persistence.Enumerated;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Ranking {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  public int id;
  
  @NotNull
  public Integer rank;

  @NotBlank
  public String fullName;

  @NotNull
  public Integer numberOfMergedPullRequests;

  @NotNull
  @Enumerated(EnumType.STRING)
  public RankingScope rankingScope;
}

package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class RankingList {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @OneToMany(cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  private List<Ranking> rankings = new ArrayList<>();

  @NotNull
  public ZonedDateTime calculationDate;

  @NotNull
  @Enumerated(EnumType.STRING)
  public RankingScope rankingScope;

  public RankingList() {}

  public RankingList(List<Ranking> rankings, ZonedDateTime calculationDate, RankingScope rankingScope) {
    this.rankings = rankings;
    this.calculationDate = calculationDate;
    this.rankingScope = rankingScope;
  }

  public List<Ranking> getRankings() {
    return rankings
      .stream()
      .sorted((r1, r2) -> {
        int result = r2.getScore().compareTo(r1.getScore());

        if (result == 0) {
          result = r1.user.username.toLowerCase().compareTo(r2.user.username.toLowerCase());
        }

        return result;
      })
      .collect(Collectors.toList());
  }
}

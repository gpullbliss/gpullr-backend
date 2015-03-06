package com.devbliss.gpullr.domain;

import java.util.Optional;

public enum RankingScope {

  TODAY(Optional.of(1)),

  LAST_7_DAYS(Optional.of(7)),

  LAST_30_DAYS(Optional.of(30)),

  ALL_TIME(Optional.empty());

  public final Optional<Integer> daysInPast;

  private RankingScope(Optional<Integer> daysInPast) {
    this.daysInPast = daysInPast;
  }
}

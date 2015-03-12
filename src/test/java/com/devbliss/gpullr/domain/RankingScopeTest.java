package com.devbliss.gpullr.domain;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RankingScopeTest {

  @Test
  public void parse() {
    assertEquals(RankingScope.TODAY, RankingScope.parse("today"));
    assertEquals(RankingScope.LAST_7_DAYS, RankingScope.parse("last_7_days"));
    assertEquals(RankingScope.LAST_30_DAYS, RankingScope.parse("last_30_days"));
    assertEquals(RankingScope.ALL_TIME, RankingScope.parse("all_time"));
  }
}

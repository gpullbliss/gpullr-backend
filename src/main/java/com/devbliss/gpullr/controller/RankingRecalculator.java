package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.service.RankingService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Triggers recalculation of rankings every five minutes.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class RankingRecalculator extends AbstractFixedScheduleWorker {

  @Autowired
  private RankingService rankingService;

  @Override
  protected void execute() {
    rankingService.recalculateRankings();
  }

  /**
   * 
   */
  @Override
  protected Date nextExecution() {
    return Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));
  }
}

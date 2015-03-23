package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

/**
 * Abstract superclass for workers that do their work in a fixed interval, e.g. every two hours.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public abstract class AbstractFixedScheduleWorker {

  private static final String NEXT_FETCH_PATTERN = "dd.MM.yyyy HH:mm:ss";

  /**
   * Calculates the date of the next execution.
   * 
   * @return date in future.
   */
  protected abstract Date nextExecution();

  /**
   * Actual execution method to be implemented by concrete class. Do not call this directly - call 
   * {@link #startFetchLoop()} instead!
   * 
   */
  protected abstract void execute();

  private TaskScheduler taskScheduler;

  @Log
  Logger logger;

  @Autowired
  public void setTaskScheduler(TaskScheduler executor) {
    this.taskScheduler = executor;
  }

  public void startFetchLoop() {
    logger.info(getClass().getSimpleName() + " starts working...");

    try {
      execute();
    } finally {
      Date nextFetch = nextExecution();
      taskScheduler.schedule(() -> startFetchLoop(), nextFetch);
      logger.info(getClass().getSimpleName()
          + " finished its work, next execution is scheduled for "
          + formatNextFetch(nextFetch));
    }
  }

  private String formatNextFetch(Date nextFetch) {
    return new SimpleDateFormat(NEXT_FETCH_PATTERN).format(nextFetch);
  }
}

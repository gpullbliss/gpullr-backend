package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

/**
 * Abstract superclass for fetcher that fetch in a fixed interval, e.g. every two hours.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public abstract class AbstractFixedScheduleFetcher {

  private static final String NEXT_FETCH_PATTERN = "dd.MM.yyyy HH:mm:ss";

  protected abstract Date nextFetch();

  protected abstract void fetch();

  private TaskScheduler taskScheduler;

  @Log
  Logger logger;

  @Autowired
  public void setTaskScheduler(TaskScheduler executor) {
    this.taskScheduler = executor;
  }

  public void startFetchLoop() {
    logger.info(getClass().getSimpleName() + " starts fetching from GitHub...");
    fetch();
    Date nextFetch = nextFetch();
    taskScheduler.schedule(() -> startFetchLoop(), nextFetch);
    logger.info(getClass().getSimpleName()
        + " finished fetching from GitHub, next fetch scheduled for "
        + formatNextFetch(nextFetch));
  }

  private String formatNextFetch(Date nextFetch) {
    return new SimpleDateFormat(NEXT_FETCH_PATTERN).format(nextFetch);
  }
}

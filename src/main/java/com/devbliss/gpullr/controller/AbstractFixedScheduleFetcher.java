package com.devbliss.gpullr.controller;

import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import com.devbliss.gpullr.util.Log;
import org.slf4j.Logger;
import java.util.Date;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

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

  private ThreadPoolTaskScheduler executor;

  @Log
  Logger logger;

  @Autowired
  public void setThreadPoolTaskScheduler(ThreadPoolTaskScheduler executor) {
    this.executor = executor;
    this.executor.initialize();
  }

  public void startFetchLoop() {
    logger.info(getClass().getSimpleName() + " starts fetching from GitHub...");
    fetch();
    Date nextFetch = nextFetch();
    executor.schedule(() -> startFetchLoop(), nextFetch);
    logger.info(getClass().getSimpleName()
        + " finished fetching from GitHub, next fetch scheduled for "
        + formatNextFetch(nextFetch));
  }

  private String formatNextFetch(Date nextFetch) {
    return new SimpleDateFormat(NEXT_FETCH_PATTERN).format(nextFetch);
  }
}

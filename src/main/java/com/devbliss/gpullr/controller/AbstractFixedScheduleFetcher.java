package com.devbliss.gpullr.controller;

import java.text.SimpleDateFormat;

import com.devbliss.gpullr.util.Log;
import org.slf4j.Logger;
import java.util.Date;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public abstract class AbstractFixedScheduleFetcher {

  private static final String NEXT_FETCH_PATTERN = "dd.MM.yyyy HH:mm:ss";

  protected abstract Date nextFetch();

  protected abstract void fetch();

  private final ThreadPoolTaskScheduler executor;

  @Log
  private Logger logger;

  protected AbstractFixedScheduleFetcher() {
    executor = new ThreadPoolTaskScheduler();
    executor.initialize();
  }

  public void startFetchLoop() {
    logger.info(getClass().getSimpleName() + " starts fetching from GitHub...");
    fetch();
    Date nextFetch = nextFetch();
    logger.info(getClass().getSimpleName()
        + " finished fetching from GitHub, next fetch scheduled for "
        + formatNextFetch(nextFetch));
    executor.schedule(() -> fetch(), nextFetch);
  }

  private String formatNextFetch(Date nextFetch) {
    return new SimpleDateFormat(NEXT_FETCH_PATTERN).format(nextFetch);
  }
}

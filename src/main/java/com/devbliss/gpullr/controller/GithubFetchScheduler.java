package com.devbliss.gpullr.controller;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import com.devbliss.gpullr.service.github.PullRequestAssigneeWatcher;
import java.time.Instant;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Starts / coordinates the fetching of data from GithubAPI. Starts fetching repos and users first, and after (
 * {@link #DELAYED_TASK_START_AFTER_SECONDS}) seconds fetching the pull requests for the repos.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class GithubFetchScheduler {

  private static final int DELAYED_TASK_START_AFTER_SECONDS = 30;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(GithubFetchScheduler.class);

  @Autowired
  private ThreadPoolTaskScheduler executor;

  @Autowired
  private GithubReposFetcher githubReposRefresher;

  @Autowired
  private GithubEventFetcher githubEventFetcher;

  @Autowired
  private GithubUserFetcher githubUserFetcher;

  @Autowired
  private PullRequestAssigneeWatcher pullrequestAssigneeWatcher;

  @Autowired
  private RankingRecalculator rankingRecalculator;

  public GithubFetchScheduler() {
  }

  @PostConstruct
  public void startExecution() {
    Date eventFetchStart = Date.from(Instant.now().plusSeconds(DELAYED_TASK_START_AFTER_SECONDS));
    Date rankingCalculationStart = Date.from(Instant.now().plusSeconds(DELAYED_TASK_START_AFTER_SECONDS * 2));
    executor.execute(() -> githubReposRefresher.startFetchLoop());
    executor.execute(() -> githubUserFetcher.startFetchLoop());
    LOGGER.debug("********** eventFetchStart: " + eventFetchStart);
    LOGGER.debug("********** rankingCalculationStart: " + rankingCalculationStart);
    executor.schedule(() -> startFetchEventsLoop(), eventFetchStart);
    LOGGER.debug("scheduled started fetch events loop");
    executor.schedule(() -> rankingRecalculator.startFetchLoop(), rankingCalculationStart);
    LOGGER.debug("scheduled recalculate ranking loop");
  }

  private void startFetchEventsLoop() {
    githubEventFetcher.startFetchEventsLoop();
  }
}

package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.service.github.PullRequestAssigneeWatcher;
import java.time.Instant;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Starts / coordinates the different scheduled workers that e.g. fetch data from GithubAPI. 
 * Starts fetching repos and users first, and after (
 * {@link #DELAYED_TASK_START_AFTER_SECONDS}) seconds fetching the pull requests for the repos and
 * finally the recalculation of rankings.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class SchedulerLauncher {

  private static final int DELAYED_TASK_START_AFTER_SECONDS = 30;

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

  public SchedulerLauncher() {}

  @PostConstruct
  public void startExecution() {
    Date eventFetchStart = Date.from(Instant.now().plusSeconds(DELAYED_TASK_START_AFTER_SECONDS));
    Date rankingCalculationStart = Date.from(Instant.now().plusSeconds(DELAYED_TASK_START_AFTER_SECONDS * 2));
    executor.execute(() -> githubReposRefresher.startFetchLoop());
    executor.execute(() -> githubUserFetcher.startFetchLoop());
    executor.schedule(() -> startFetchEventsLoop(), eventFetchStart);
    executor.schedule(() -> rankingRecalculator.startFetchLoop(), rankingCalculationStart);
  }

  private void startFetchEventsLoop() {
    githubEventFetcher.startFetchEventsLoop();
  }
}
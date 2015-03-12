package com.devbliss.gpullr.controller;

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

  private static final int DELAYED_TASK_START_AFTER_SECONDS = 60;

  private ThreadPoolTaskScheduler executor;

  @Autowired
  private GithubReposFetcher githubReposRefresher;

  @Autowired
  private GithubEventFetcher githubEventFetcher;

  @Autowired
  private GithubUserFetcher githubUserFetcher;

  @Autowired
  private PullRequestAssigneeWatcher pullrequestAssigneeWatcher;

  public GithubFetchScheduler() {
    executor = new ThreadPoolTaskScheduler();
    executor.initialize();
  }

  @PostConstruct
  public void startExecution() {
    Date delayedTaskStart = Date.from(Instant.now().plusSeconds(DELAYED_TASK_START_AFTER_SECONDS));
    executor.execute(githubReposRefresher::startFetchLoop);
    executor.execute(githubUserFetcher::startFetchLoop);
    executor.schedule(this::startFetchEventsLoop, delayedTaskStart);
  }

  private void startFetchEventsLoop() {
    githubEventFetcher.startFetchEventsLoop();
  }
}

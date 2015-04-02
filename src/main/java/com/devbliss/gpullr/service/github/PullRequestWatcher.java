package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.util.Log;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Holds threads refreshing certain data for each open pull request which are not delivered in the regular
 * pull request event data.
 * 
 * Reason: pull request events returned by GitHub API do NOT contain the assignee in all cases.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PullRequestWatcher {

  @Log
  Logger logger;

  private final TaskScheduler taskScheduler;

  private final PullRequestWatchThreadProducer pullRequestWatchThreadProducer;

  /**
   * Map with pullRequest-id as key and the watcher thread as value
   */
  private final Map<Integer, PullRequestWatchThread> activeWatchers = new HashMap<>();

  @Autowired
  public PullRequestWatcher(
      TaskScheduler taskScheduler,
      PullRequestWatchThreadProducer pullRequestWatchThreadProducer) {
    this.taskScheduler = taskScheduler;
    this.pullRequestWatchThreadProducer = pullRequestWatchThreadProducer;
  }

  /**
   * Starts an assignee watcher for the given pull request which periodically fetches
   * certain data for the PR from GitHub API.
   * 
   * Does nothing in case there is already such a watcher for the given pull request
   *   
   * @param pullRequest pull request to watch the assignee for
   */
  public void startWatching(PullRequest pullRequest) {
    PullRequestWatchThread thread = activeWatchers.get(pullRequest.id);

    if (thread == null) {
      synchronized (PullRequestWatcher.class) {
        thread = activeWatchers.get(pullRequest.id);

        if (thread == null) {
          thread = pullRequestWatchThreadProducer.createThread(pullRequest);
          activeWatchers.put(pullRequest.id, thread);
          taskScheduler.schedule(thread, Date.from(Instant.now()));
          logger.debug("started assignee watcher for pull request " + pullRequest + " thread: " + this);
        }
      }
    }
  }

  /**
   * Stops the assignee watcher for the given pull request (started with {@link #startWatching(PullRequest)}) in 
   * case there is one, or does nothing there isn't.
   * 
   * @param pullRequest pull request to stop the watcher for
   */
  public void stopWatching(PullRequest pullRequest) {
    PullRequestWatchThread watcherToStop = activeWatchers.remove(pullRequest.id);

    if (watcherToStop != null) {
      watcherToStop.pleaseStop();
      logger.debug("stopped assignee watcher for pull request " + pullRequest);
    }
  }
}

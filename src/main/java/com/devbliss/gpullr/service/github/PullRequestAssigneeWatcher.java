package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestService;
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
 * Holds threads refreshing the assignee for each open pull request.
 * Reason: pull request events returned by GitHub API do NOT contain the assignee in all cases.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PullRequestAssigneeWatcher {

  @Log
  private Logger logger;

  private final TaskScheduler taskScheduler;

  private final GithubApi githubApi;

  private final PullRequestService pullRequestService;

  /**
   * Map with pullRequest-id as key and the watcher thread as value
   */
  private final Map<Integer, PullRequestAssigneeWatchThread> activeWatchers = new HashMap<>();

  @Autowired
  public PullRequestAssigneeWatcher(
      TaskScheduler taskScheduler,
      GithubApi githubApi,
      PullRequestService pullRequestService) {
    this.taskScheduler = taskScheduler;
    this.githubApi = githubApi;
    this.pullRequestService = pullRequestService;
  }

  /**
   * Starts an assignee watcher for the given pull request which periodically fetches
   * the current assignee for the PR from GitHub API.
   * 
   * Does nothing in case there is already such a watcher for the given pull request
   *   
   * @param pullRequest pull request to watch the assignee for
   */
  public void startWatching(PullRequest pullRequest) {
    PullRequestAssigneeWatchThread thread = activeWatchers.get(pullRequest.id);

    if (thread == null) {
      synchronized (PullRequestAssigneeWatcher.class) {
        thread = activeWatchers.get(pullRequest.id);

        if (thread == null) {
          thread = new PullRequestAssigneeWatchThread(pullRequest, taskScheduler, githubApi, pullRequestService);
          activeWatchers.put(pullRequest.id, thread);
          taskScheduler.schedule(thread, Date.from(Instant.now()));
          logger.debug("started assignee watcher for pullrequest " + pullRequest + " thread: " + this);
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
    PullRequestAssigneeWatchThread watcherToStop = activeWatchers.remove(pullRequest.id);

    if (watcherToStop != null) {
      watcherToStop.pleaseStop();
      logger.debug("stopped assignee watcher for pullrequest " + pullRequest);
    }
  }
}

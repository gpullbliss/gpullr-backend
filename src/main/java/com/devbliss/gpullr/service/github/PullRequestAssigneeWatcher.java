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

  public void startWatching(PullRequest pullRequest) {
    PullRequestAssigneeWatchThread thread = activeWatchers.get(pullRequest.id);

    if (thread == null) {
      synchronized (PullRequestAssigneeWatcher.class) {
        thread = activeWatchers.get(pullRequest.id);

        if (thread == null) {
          thread = createThread(pullRequest);
          activeWatchers.put(pullRequest.id, thread);
          taskScheduler.schedule(thread, Date.from(Instant.now()));
          logger.debug("started assignee watcher for pullrequest " + pullRequest + " thread: " + this);
        }
      }
    }
  }

  public void stopWatching(PullRequest pullRequest) {
    PullRequestAssigneeWatchThread watcherToStop = activeWatchers.remove(pullRequest.id);

    if (watcherToStop != null) {
      watcherToStop.pleaseStop();
      logger.debug("stopped assignee watcher for pullrequest " + pullRequest);
    }
  }

  private PullRequestAssigneeWatchThread createThread(PullRequest pullRequest) {
    return new PullRequestAssigneeWatchThread(pullRequest, taskScheduler, githubApi, pullRequestService);
  }
}

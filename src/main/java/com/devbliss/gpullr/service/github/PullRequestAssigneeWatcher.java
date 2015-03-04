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

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PullRequestAssigneeWatcher {

  @Log
  private Logger logger;

  private TaskScheduler taskScheduler;

  private GithubApi githubApi;

  private final Map<Integer, PullRequestAssigneeWatchThread> watchedPullRequests = new HashMap<>();

  @Autowired
  public PullRequestAssigneeWatcher(TaskScheduler taskScheduler, GithubApi githubApi) {
    this.taskScheduler = taskScheduler;
    this.githubApi = githubApi;
  }

  public void startWatching(PullRequest pullRequest) {
    PullRequestAssigneeWatchThread thread = createThread(pullRequest);
    watchedPullRequests.put(pullRequest.id, thread);
    taskScheduler.schedule(thread, Date.from(Instant.now()));
    logger.debug("started assignee watcher for pullrequest " + pullRequest);
  }

  public void stopWatching(PullRequest pullRequest) {
    PullRequestAssigneeWatchThread watcherToStop = watchedPullRequests.remove(pullRequest.id);

    if (watcherToStop != null) {
      watcherToStop.pleaseStop();
      logger.debug("stopped assignee watcher for pullrequest " + pullRequest);
    }
  }

  private PullRequestAssigneeWatchThread createThread(PullRequest pullRequest) {
    return new PullRequestAssigneeWatchThread(pullRequest, taskScheduler, githubApi);
  }
}

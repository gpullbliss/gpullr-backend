package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Creates instances of {@link PullRequestAssigneeWatchThread}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class PullRequestAssigneeWatchThreadProducer {

  @Autowired
  private TaskScheduler taskScheduler;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private PullRequestService pullRequestService;

  public PullRequestAssigneeWatchThread createThread(PullRequest pullRequest) {
    return new PullRequestAssigneeWatchThread(pullRequest.id, taskScheduler, githubApi, pullRequestService);
  }
}

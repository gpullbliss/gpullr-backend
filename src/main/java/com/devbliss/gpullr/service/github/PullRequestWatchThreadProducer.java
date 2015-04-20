package com.devbliss.gpullr.service.github;

import static com.devbliss.gpullr.util.Constants.QUALIFIER_TASK_SCHEDULER;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Creates instances of {@link PullRequestWatchThread}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class PullRequestWatchThreadProducer {

  @Autowired
  @Qualifier(QUALIFIER_TASK_SCHEDULER)
  private TaskScheduler taskScheduler;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private PullRequestService pullRequestService;

  public PullRequestWatchThread createThread(PullRequest pullRequest) {
    return new PullRequestWatchThread(pullRequest.id, taskScheduler, githubApi, pullRequestService);
  }
}

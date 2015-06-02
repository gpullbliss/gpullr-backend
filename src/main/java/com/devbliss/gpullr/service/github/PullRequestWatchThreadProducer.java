package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.CommitService;
import com.devbliss.gpullr.service.PullRequestService;
import org.springframework.beans.factory.annotation.Autowired;
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
  private TaskScheduler taskScheduler;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private PullRequestService pullRequestService;

  @Autowired
  private CommitService commitService;

  public PullRequestWatchThread createThread(PullRequest pullRequest) {
    return new PullRequestWatchThread(
        pullRequest.id,
        taskScheduler,
        githubApi,
        pullRequestService,
        commitService);
  }
}

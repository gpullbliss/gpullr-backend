package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestCommentService;
import com.devbliss.gpullr.service.PullRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Creates instances of {@link PullRequestWatchThread}.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class PullRequestWatchThreadProducer {

  private TaskScheduler taskScheduler;

  private GithubApi githubApi;

  private PullRequestService pullRequestService;

  private PullRequestCommentService pullRequestCommentService;

  @Autowired
  public PullRequestWatchThreadProducer(TaskScheduler taskScheduler,
      GithubApi githubApi,
      PullRequestService pullRequestService,
      PullRequestCommentService pullRequestCommentService) {
    this.taskScheduler = taskScheduler;
    this.githubApi = githubApi;
    this.pullRequestService = pullRequestService;
    this.pullRequestCommentService = pullRequestCommentService;
  }

  public PullRequestWatchThread createThread(PullRequest pullRequest) {
    return new PullRequestWatchThread(pullRequest.id, taskScheduler, githubApi, pullRequestService, pullRequestCommentService);
  }
}

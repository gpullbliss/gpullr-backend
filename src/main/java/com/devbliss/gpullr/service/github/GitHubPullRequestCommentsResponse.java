package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequestComment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class GitHubPullRequestCommentsResponse extends AbstractGithubResponse<List<PullRequestComment>> {

  public GitHubPullRequestCommentsResponse(
      List<PullRequestComment> pullRequestComments,
      Instant nextFetch,
      Optional<String> etagHeader) {
    super(pullRequestComments, nextFetch, etagHeader);
  }
}

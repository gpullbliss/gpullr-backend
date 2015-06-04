package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Comment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class GitHubPullRequestCommentsResponse extends AbstractGithubResponse<List<Comment>> {

  public GitHubPullRequestCommentsResponse(
      List<Comment> comments,
      Instant nextFetch,
      Optional<String> etagHeader) {
    super(comments, nextFetch, etagHeader);
  }
}

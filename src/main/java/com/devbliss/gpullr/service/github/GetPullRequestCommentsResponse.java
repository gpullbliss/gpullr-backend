package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequestComment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Created by andre on 28/05/15.
 */
public class GetPullRequestCommentsResponse extends AbstractGithubResponse<List<PullRequestComment>> {

  public GetPullRequestCommentsResponse(
      List<PullRequestComment> pullRequestComments,
      Instant nextFetch,
      Optional<String> etagHeader) {
    super(pullRequestComments, nextFetch, etagHeader);
  }
}

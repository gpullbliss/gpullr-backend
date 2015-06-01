package com.devbliss.gpullr.service.github.commits;

import com.devbliss.gpullr.domain.Commit;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.github.AbstractGithubResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class GetPullRequestCommitsResponse extends AbstractGithubResponse<List<Commit>> {

  public GetPullRequestCommitsResponse(
      List<Commit> commits,
      Instant nextFetch,
      Optional<String> etagHeader) {
    super(commits, nextFetch, etagHeader);
  }
}

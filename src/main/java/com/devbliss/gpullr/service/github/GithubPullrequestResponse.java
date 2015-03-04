package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import java.util.Optional;

public class GithubPullrequestResponse extends AbstractGithubResponse<Optional<PullRequest>> {

  protected GithubPullrequestResponse(
      Optional<PullRequest> payload,
      int nextRequestAfterSeconds,
      Optional<String> etagHeader) {
    super(payload, nextRequestAfterSeconds, etagHeader);
  }

}

package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import java.util.Optional;

/**
 * Response to a request that has fetched the details of ONE single pullrequest from GitHub API.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubPullrequestResponse extends AbstractGithubResponse<Optional<PullRequest>> {

  public GithubPullrequestResponse(
      Optional<PullRequest> payload,
      int nextRequestAfterSeconds,
      Optional<String> etagHeader) {
    super(payload, nextRequestAfterSeconds, etagHeader);
  }
}

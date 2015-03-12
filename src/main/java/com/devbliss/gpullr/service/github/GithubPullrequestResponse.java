package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import java.time.Instant;
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
      Instant nextRequest,
      Optional<String> etagHeader) {
    super(payload, nextRequest, etagHeader);
  }
}

package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import java.time.Instant;
import java.util.Optional;

/**
 * Response to a request that has fetched the details of ONE single pull request from GitHub API.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubPullerRequestResponse extends AbstractGithubResponse<Optional<PullRequest>> {

  public GithubPullerRequestResponse(
      Optional<PullRequest> payload,
      Instant nextFetch,
      Optional<String> etagHeader) {
    super(payload, nextFetch, etagHeader);
  }
}

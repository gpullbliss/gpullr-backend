package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import java.util.Optional;

/**
 * Request against GitHub API to fetch the continuous integration (e.g. Jenkins) build status for a pull request. 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GetPullRequestBuildStatusRequest extends AbstractGithubRequest {

  private static final String URI_TEMPLATE = "https://api.github.com/repos/devbliss/%s/statuses/%s";

  private final PullRequest pullRequest;

  public GetPullRequestBuildStatusRequest(PullRequest pullRequest, Optional<String> etagHeader) {
    super(etagHeader);
    this.pullRequest = pullRequest;
  }

  @Override
  protected String createUri(int page) {
    return String.format(URI_TEMPLATE, pullRequest.repo.name, pullRequest.branchName);
  }
}

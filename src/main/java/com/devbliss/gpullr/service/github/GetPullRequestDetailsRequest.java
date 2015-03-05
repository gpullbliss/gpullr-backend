package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import java.util.Optional;

/**
 * Request against GitHub API to fetch data of ONE pull request. 
 * Necessary since the assignee is not correctly set in pullRequest-OPENED-event-payload.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GetPullRequestDetailsRequest extends AbstractGithubRequest {

  private static final String URI_TEMPLATE = "https://api.github.com/repos/devbliss/%s/pulls/%d";

  private final PullRequest pullRequest;

  public GetPullRequestDetailsRequest(PullRequest pullRequest, Optional<String> etagHeader) {
    super(etagHeader);
    this.pullRequest = pullRequest;
  }

  @Override
  protected String createUri(int page) {
    return String.format(URI_TEMPLATE, pullRequest.repo.name, pullRequest.number);
  }
}
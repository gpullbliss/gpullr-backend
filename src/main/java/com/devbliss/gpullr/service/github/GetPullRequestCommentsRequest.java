package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import java.util.Optional;

/**
 * Request against the GitHub API to fetch all comments for a pull request
 */
public class GetPullRequestCommentsRequest extends AbstractGithubRequest {

  private static final String URI_TEMPLATE = "https://api.github.com/repos/devbliss/%s/pulls/comments/%d";

  private PullRequest pullRequest;

  public GetPullRequestCommentsRequest(Optional<String> etagHeader, int page, PullRequest pullRequest) {
    super(etagHeader, page);
    this.pullRequest = pullRequest;
  }

  @Override protected String createUri(int page) {
    return String.format(URI_TEMPLATE, pullRequest.repo.name, pullRequest.number);
  }
}

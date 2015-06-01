package com.devbliss.gpullr.service.github.commits;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.github.AbstractGithubRequest;
import java.util.Optional;

public class GetPullRequestCommitsRequest extends AbstractGithubRequest {

  private static final String URI_TEMPLATE = "https://api.github.com/repos/devbliss/%s/pulls/%d/commits";

  private static final String URI_TEMPLATE_WITH_PAGE =
      "https://api.github.com/repos/devbliss/%s/pulls/%d/commits?page=%d";

  private final PullRequest pullRequest;

  public GetPullRequestCommitsRequest(PullRequest pullRequest, Optional<String> etagHeader) {
    this(pullRequest, etagHeader, 0);
  }

  public GetPullRequestCommitsRequest(PullRequest pullRequest, Optional<String> etagHeader, int page) {
    super(etagHeader, page);
    this.pullRequest = pullRequest;
  }

  public GetPullRequestCommitsRequest requestForNextPage() {
    return new GetPullRequestCommitsRequest(pullRequest, etagHeader, page + 1);
  }

  @Override
  protected String createUri(int page) {
    if (page > 0) {
      return String.format(URI_TEMPLATE_WITH_PAGE, pullRequest.repo.name, pullRequest.number, page);
    } else {
      return String.format(URI_TEMPLATE, pullRequest.repo.name, pullRequest.number);
    }
  }
}

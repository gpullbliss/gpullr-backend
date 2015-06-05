package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Repo;
import java.util.Optional;

/**
 * Request against GitHub API to fetch data of all pull requests for a given repository.
 * Necessary since the not all pull requests are fetched otherwise and incoming comment events cannot be matched
 * to the corresponding pull request.
 *
 * @author Alexander Blüm <alexander.bluem@devbliss.com>
 */
public class GetAllPullRequestsDetailsRequest extends AbstractGithubRequest {

  private static final String URI_TEMPLATE = "https://api.github.com/repos/devbliss/%s/pulls";

  private final Repo repo;
  private final Optional<String> etagHeader;

  public GetAllPullRequestsDetailsRequest(Repo repo, Optional<String> etagHeader) {
    super(etagHeader);
    this.repo = repo;
    this.etagHeader = etagHeader;
  }

  @Override
  protected String createUri(int page) {
    return String.format(URI_TEMPLATE, repo.name);
  }
}

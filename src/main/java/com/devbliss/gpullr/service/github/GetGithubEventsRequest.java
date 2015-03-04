package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Repo;
import java.util.Optional;

/**
 * Request against GitHub API for fetching all events of a certain repository.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GetGithubEventsRequest extends AbstractGithubRequest {

  private static final String URI_TEMPLATE = "https://api.github.com/repos/devbliss/%s/events";

  private static final String URI_TEMPLATE_WITH_PAGE = "https://api.github.com/repos/devbliss/%s/events?page=%s";

  private final Repo repo;

  public GetGithubEventsRequest(Repo repo, Optional<String> etagHeader, int page) {
    super(etagHeader, page);
    this.repo = repo;
  }

  /**
   * Returns a new instance with same values as this one but with page parameter incremented by one.
   * 
   * @return
   */
  public GetGithubEventsRequest requestForNextPage() {
    return new GetGithubEventsRequest(repo, etagHeader, page + 1);
  }

  @Override
  protected String createUri(int page) {
    if (page > 0) {
      return String.format(URI_TEMPLATE_WITH_PAGE, repo.name, page);
    } else {
      return String.format(URI_TEMPLATE, repo.name);
    }
  }
}

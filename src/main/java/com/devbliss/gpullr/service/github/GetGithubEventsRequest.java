package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Repo;
import java.net.URI;
import java.util.Optional;
import org.apache.http.client.methods.HttpGet;

/**
 * Request against GitHub API for fetching all events of a certain repository.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GetGithubEventsRequest extends HttpGet {

  private static final String HEADER_ETAG = "If-None-Match";

  private static final String URI_TEMPLATE = "https://api.github.com/repos/devbliss/%s/events";

  private static final String URI_TEMPLATE_WITH_PAGE = "https://api.github.com/repos/devbliss/%s/events?page=%s";

  private final Repo repo;

  private final Optional<String> etagHeader;

  private final int page;

  public GetGithubEventsRequest(Repo repo, Optional<String> etagHeader, int page) {
    this.repo = repo;
    this.etagHeader = etagHeader;
    this.page = page;
    configure();
  }

  public GetGithubEventsRequest nextPage() {
    return new GetGithubEventsRequest(repo, etagHeader, page + 1);
  }

  private void configure() {
    String uri;

    if (page > 0) {
      uri = String.format(URI_TEMPLATE_WITH_PAGE, repo.name, page);
    } else {
      uri = String.format(URI_TEMPLATE, repo.name);
    }

    setURI(URI.create(uri));
    etagHeader.ifPresent(s -> setHeader(HEADER_ETAG, s));
  }

}

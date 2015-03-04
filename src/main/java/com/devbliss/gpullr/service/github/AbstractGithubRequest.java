package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.util.http.GithubHttpClient;
import java.net.URI;
import java.util.Optional;
import org.apache.http.client.methods.HttpGet;

/**
 * Abstract superclass for requests against GitHub API using {@link GithubHttpClient}.
 * Supports paging and ETAG headers.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public abstract class AbstractGithubRequest extends HttpGet {

  private static final String HEADER_ETAG = "If-None-Match";

  protected final Optional<String> etagHeader;

  protected final int page;

  protected abstract String createUri(int page);

  protected AbstractGithubRequest(Optional<String> etagHeader, int page) {
    this.etagHeader = etagHeader;
    this.page = page;
  }

  protected AbstractGithubRequest(Optional<String> etagHeader) {
    this(etagHeader, 0);
  }

  protected void configure() {
    setURI(URI.create(createUri(page)));
    etagHeader.ifPresent(s -> setHeader(HEADER_ETAG, s));
  }
}

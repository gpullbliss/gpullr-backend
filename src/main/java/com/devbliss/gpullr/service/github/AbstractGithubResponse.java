package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.util.http.GithubHttpResponse;

import java.time.Instant;
import java.util.Optional;

/**
 * Abstract superclass for application level responses from GitHub API. 
 * Contains the information when the next poll is allowed and the ETAG header plus the actual payload.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public abstract class AbstractGithubResponse<PAYLOAD> {

  public final Instant nextFetch;

  public final Optional<String> etagHeader;

  public final PAYLOAD payload;

  protected AbstractGithubResponse(PAYLOAD payload, Instant nextFetch, Optional<String> etagHeader) {
    this.payload = payload;
    this.nextFetch = nextFetch;
    this.etagHeader = etagHeader;
  }
  
  protected AbstractGithubResponse(PAYLOAD payload, GithubHttpResponse response) {
    this.payload = payload;
    this.nextFetch = response.getNextFetch();
    this.etagHeader = response.getEtag();
  }
}

package com.devbliss.gpullr.service.github;

import java.util.Optional;

/**
 * Abstract superclass for application level responses from GitHub API. 
 * Contains the information when the next poll is allowed and the ETAG header plus the actual payload.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public abstract class AbstractGithubResponse<PAYLOAD> {

  public final int nextRequestAfterSeconds;
  
  public final Optional<String> etagHeader;
  
  public final PAYLOAD payload;

  protected AbstractGithubResponse(PAYLOAD payload, final int nextRequestAfterSeconds, Optional<String> etagHeader) {
    this.payload = payload;
    this.nextRequestAfterSeconds = nextRequestAfterSeconds;
    this.etagHeader = etagHeader;
  }
}

package com.devbliss.gpullr.util.http;

import com.devbliss.gpullr.service.github.AbstractGithubRequest;

/**
 * Implementation that does no real calls. To be used as mock, for tests etc.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubHttpClientImplNoop implements GithubHttpClient {

  @Override
  public GithubHttpResponse execute(AbstractGithubRequest request) {
    return null;
  }
}

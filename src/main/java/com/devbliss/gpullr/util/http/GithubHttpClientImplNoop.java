package com.devbliss.gpullr.util.http;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Implementation that does no real calls. To be used as mock, for tests etc.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubHttpClientImplNoop implements GithubHttpClient {

  @Override
  public GithubHttpResponse execute(HttpUriRequest request) {
    return null;
  }
}

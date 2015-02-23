package com.devbliss.gpullr.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Implementation that does no real calls. To be used as mock, for tests etc.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubClientImplNoop implements GithubClient {

  @Override
  public HttpResponse execute(HttpUriRequest request) {
    return null;
  }

}

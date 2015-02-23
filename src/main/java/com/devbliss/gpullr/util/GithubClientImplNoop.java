package com.devbliss.gpullr.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public class GithubClientImplNoop implements GithubClient {

  @Override
  public HttpResponse execute(HttpUriRequest request) {
    return null;
  }

}

package com.devbliss.gpullr.util;

import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubClientImpl implements GithubClient {

  private HttpClient httpClient = HttpClientBuilder.create().build();

  @Override
  public HttpResponse execute(HttpUriRequest request) {
    try {
      return httpClient.execute(request);
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

}

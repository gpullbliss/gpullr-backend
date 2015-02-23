package com.devbliss.gpullr.util;

import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation that performs "real" calls and adds Authorization header with 
 * application's oauth token to every request.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class GithubClientImpl implements GithubClient {

  @Value("${github.oauthtoken}")
  private String oauthToken;

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

  private HttpClient httpClient = HttpClientBuilder.create().build();

  @Override
  public HttpResponse execute(HttpUriRequest request) {
    try {
      request.setHeader(AUTHORIZATION_HEADER_KEY, "token " + oauthToken);
      return httpClient.execute(request);
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

}

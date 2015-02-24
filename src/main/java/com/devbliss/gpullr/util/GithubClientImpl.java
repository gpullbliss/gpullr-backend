package com.devbliss.gpullr.util;

import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import java.util.stream.Stream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Qualifier("githubClientImpl")
public class GithubClientImpl implements GithubClient {

  @Log
  private Logger logger;

  @Value("${github.oauthtoken}")
  private String oauthToken;

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

  private HttpClient httpClient = HttpClientBuilder.create().build();

  @Override
  public HttpResponse execute(HttpUriRequest req) {
    try {
      logger.debug("*** FETCH REQ: " + req.getURI());
      req.setHeader(AUTHORIZATION_HEADER_KEY, "token " + oauthToken);
      Stream.of(req.getAllHeaders()).forEach(h -> logger.debug("\t\t" + h.getName() + ": " + h.getValue()));
      HttpResponse resp = httpClient.execute(req);
      logger.debug("*** FETCH RES: " + req.getURI() + " / " + resp.getStatusLine().getStatusCode());
      return resp;
    } catch (IOException e) {
      logger.error("Exception executing request: " + e.getLocalizedMessage(), e);
      throw new UnexpectedException(e);
    }
  }
}

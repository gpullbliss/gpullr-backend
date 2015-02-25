package com.devbliss.gpullr.util;

import com.devbliss.gpullr.exception.UnexpectedException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
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
@Scope(value = "thread", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GithubClientImpl implements GithubClient {

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

  private static final String REMAINING_RATE_LIMIT_HEADER_KEY = "X-RateLimit-Remaining";

  @Log
  private Logger logger;

  @Value("${github.oauthtoken}")
  private String oauthToken;

  private HttpClient httpClient;

  public GithubClientImpl() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(5);
    connectionManager.setMaxTotal(600);
    RequestConfig config = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(4001).setRedirectsEnabled(
        true).build();
    httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionManager(connectionManager).build();
  }

  @Override
  public HttpResponse execute(HttpUriRequest req) {
    try {
      req.setHeader(AUTHORIZATION_HEADER_KEY, "token " + oauthToken);
      logger.debug("HTTP request against GitHub: " + req.getURI());
      HttpResponse resp = httpClient.execute(req);
      logger.debug("HTTP response from GitHub: " + resp.getStatusLine().getStatusCode() + ", remaining rate limit: "
          + resp.getLastHeader(REMAINING_RATE_LIMIT_HEADER_KEY).getValue());
      return resp;
    } catch (Exception e) {
      e.printStackTrace();
      throw new UnexpectedException(e);
    }
  }
}

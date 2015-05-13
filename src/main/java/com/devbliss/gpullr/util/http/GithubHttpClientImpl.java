package com.devbliss.gpullr.util.http;

import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.service.github.AbstractGithubRequest;
import com.devbliss.gpullr.util.Log;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
public class GithubHttpClientImpl implements GithubHttpClient {

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

  @Log
  private Logger logger;

  @Value("${github.oauthtoken}")
  private String oauthToken;

  private CloseableHttpClient httpClient;

  private ApplicationContext applicationContext;

  @Autowired
  public GithubHttpClientImpl(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(5);
    connectionManager.setMaxTotal(600);
    RequestConfig config = RequestConfig.custom().setRedirectsEnabled(true).build();
    httpClient = HttpClientBuilder
      .create()
      .setDefaultRequestConfig(config)
      .setConnectionManager(connectionManager)
      .build();
  }

  @Override
  public GithubHttpResponse execute(AbstractGithubRequest req) {
    try {
      req.setHeader(AUTHORIZATION_HEADER_KEY, "token " + oauthToken);
      logger.debug("HTTP request against GitHub: " + req.getURI());
      CloseableHttpResponse resp = httpClient.execute(req);
      GithubHttpResponse githubResp = GithubHttpResponse.create(resp, req.getURI(), applicationContext);
      logResponse(githubResp);
      return githubResp;
    } catch (Exception e) {
      throw new UnexpectedException(e);
    }
  }

  private void logResponse(GithubHttpResponse resp) {
    logger.debug("HTTP response from GitHub: "
        + resp.getStatusCode()
        + ", remaining rate limit: "
        + resp.rateLimitRemaining
        + ", reset at: "
        + resp.getFormattedRateLimitResetTime());
  }
}

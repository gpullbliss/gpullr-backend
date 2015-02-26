package com.devbliss.gpullr.util.http;

import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.service.github.GetGithubEventsRequest;
import com.devbliss.gpullr.util.Log;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
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
public class GithubHttpClientImpl implements GithubHttpClient {

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

  private static final String REMAINING_RATE_LIMIT_HEADER_KEY = "X-RateLimit-Remaining";
  private static final String REMAINING_RATE_RESET_HEADER_KEY = "X-RateLimit-Reset";

  @Log
  private Logger logger;

  @Value("${github.oauthtoken}")
  private String oauthToken;

  private CloseableHttpClient httpClient;

  public GithubHttpClientImpl() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(5);
    connectionManager.setMaxTotal(600);
    RequestConfig config = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(4001).setRedirectsEnabled(
        true).build();
    httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionManager(connectionManager).build();
  }

  @Override
  public GithubHttpResponse execute(GetGithubEventsRequest req) {
    try {
      req.setHeader(AUTHORIZATION_HEADER_KEY, "token " + oauthToken);
      logger.debug("HTTP request against GitHub: " + req.getURI());
      CloseableHttpResponse resp = httpClient.execute(req);
      logResponse(resp);
      return GithubHttpResponse.create(resp);
    } catch (Exception e) {
      throw new UnexpectedException(e);
    }
  }

  private void logResponse(HttpResponse resp) {
    logger.debug("HTTP response from GitHub: "
        + resp.getStatusLine().getStatusCode()
        + ", remaining rate limit: "
        + resp.getLastHeader(REMAINING_RATE_LIMIT_HEADER_KEY).getValue()
        + ", reset at: "
        + parseRateLimitResetTime(resp));
  }

  private String parseRateLimitResetTime(HttpResponse resp) {
    long epoch = Long.valueOf(resp.getLastHeader(REMAINING_RATE_RESET_HEADER_KEY).getValue());
    ZonedDateTime ts = ZonedDateTime
      .ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("UTC"))
      .withZoneSameInstant(ZoneId.systemDefault());
    return ts.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }
}

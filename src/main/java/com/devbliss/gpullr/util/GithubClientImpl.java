package com.devbliss.gpullr.util;

import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import java.util.stream.Stream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(GithubClientImpl.class);

  @Value("${github.oauthtoken}")
  private String oauthToken;

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

  private HttpClient httpClient;

  public GithubClientImpl() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(5);
    connectionManager.setMaxTotal(600);
    RequestConfig config = RequestConfig.custom().setSocketTimeout(4000).setConnectTimeout(4001).setRedirectsEnabled(
        true).build();
    httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionManager(connectionManager).build();

    System.err.println("+++++++++++++++++++++++++++++ CLIENT in own thead: " + this);
    // PoolingHttpClientConnectionManager()
  }

  @Override
  public HttpResponse execute(HttpUriRequest req) {
    try {
      System.err.println("*** FETCH REQ: " + req.getURI());
      req.setHeader(AUTHORIZATION_HEADER_KEY, "token " + oauthToken);
      Stream.of(req.getAllHeaders()).forEach(h -> System.err.println("\t\t- " + h.getName() + ": " + h.getValue()));
      HttpResponse resp = httpClient.execute(req);
      System.err.println("*** FETCH RES: " + req.getURI() + " / " + resp.getStatusLine().getStatusCode());
      System.err.println("remaining rate limit: " + resp.getLastHeader("X-RateLimit-Remaining").getValue());
      System.err.println("ETAG: " + resp.getLastHeader("ETag").getValue());
      return resp;
    } catch (IOException e) {
      System.err.println("Exception executing request: " + e.getLocalizedMessage());
      e.printStackTrace();
      throw new UnexpectedException(e);
    } catch (Exception e) {
      System.err.println("Exception executing request: " + e.getLocalizedMessage());
      e.printStackTrace();
      throw new UnexpectedException(e);
    }
  }
}

package com.devbliss.gpullr.util.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

/**
 * TODO
 */
@Component
public class JsonHttpClient {

  private static final String HTTP_HEADER_KEY_ACCEPT = "Accept";
  private static final String APPLICATION_JSON = "application/json";

  private HttpClient httpClient;

  public HttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = HttpClientBuilder.create().build();
    }

    return httpClient;
  }

  public HttpPost getPostMethod(String url) {
    final HttpPost postMethod = new HttpPost(url);
    postMethod.setHeader(HTTP_HEADER_KEY_ACCEPT, APPLICATION_JSON);

    return postMethod;
  }

  public HttpGet getGetMethod(String url) {
    final HttpGet getMethod = new HttpGet(url);
    getMethod.setHeader(HTTP_HEADER_KEY_ACCEPT, APPLICATION_JSON);

    return getMethod;
  }

}

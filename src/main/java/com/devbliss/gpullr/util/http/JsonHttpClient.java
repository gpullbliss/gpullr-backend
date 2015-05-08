package com.devbliss.gpullr.util.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

/**
 * Provides methods to create necessary objects to do http requests.
 */
@Component
public class JsonHttpClient {

  private static final String HTTP_HEADER_KEY_ACCEPT = "Accept";

  private static final String APPLICATION_JSON = "application/json";

  public CloseableHttpClient getHttpClient() {
    return HttpClientBuilder.create().build();
  }

  public HttpPost getPostMethod(String url) {
    if (url == null) {
      return null;
    }

    final HttpPost postMethod = new HttpPost(url);
    postMethod.setHeader(HTTP_HEADER_KEY_ACCEPT, APPLICATION_JSON);

    return postMethod;
  }

  public HttpGet getGetMethod(String url) {
    if (url == null) {
      return null;
    }

    final HttpGet getMethod = new HttpGet(url);
    getMethod.setHeader(HTTP_HEADER_KEY_ACCEPT, APPLICATION_JSON);

    return getMethod;
  }

}

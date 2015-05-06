package com.devbliss.gpullr.util.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for {@link JsonHttpClient}
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonHttpClientUnitTest {

  private static final String HTTP_HEADER_KEY_ACCEPT = "Accept";
  private static final String APPLICATION_JSON = "application/json";

  @InjectMocks
  private JsonHttpClient jsonHttpClient;

  @Test
  public void testGetSingletonHttpClientObject() {
    final HttpClient httpClient = jsonHttpClient.getHttpClient();
    assertEquals(httpClient, jsonHttpClient.getHttpClient());
  }

  @Test
  public void testGetPostMethod() throws MalformedURLException {
    final String testUrl = "http://something.com/";
    final HttpPost postMethod = jsonHttpClient.getPostMethod(testUrl);

    assertEquals(testUrl, postMethod.getURI().toURL().toString());
    assertEquals(APPLICATION_JSON, postMethod.getHeaders(HTTP_HEADER_KEY_ACCEPT)[0].getValue());

    assertNotEquals(postMethod, jsonHttpClient.getPostMethod(testUrl));
  }

  @Test
  public void testGetPostMethodWithNull() {
    assertNull(jsonHttpClient.getPostMethod(null));
  }

  @Test
  public void testGetGetMethod() throws MalformedURLException {
    final String testUrl = "http://something.com/";
    final HttpGet getMethod = jsonHttpClient.getGetMethod(testUrl);

    assertEquals(testUrl, getMethod.getURI().toURL().toString());
    assertEquals(APPLICATION_JSON, getMethod.getHeaders(HTTP_HEADER_KEY_ACCEPT)[0].getValue());

    assertNotEquals(getMethod, jsonHttpClient.getGetMethod(testUrl));
  }

  @Test
  public void testGetGetMethodWithNull() {
    assertNull(jsonHttpClient.getGetMethod(null));
  }

}

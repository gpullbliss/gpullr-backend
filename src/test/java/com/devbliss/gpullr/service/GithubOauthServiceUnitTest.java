package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.exception.OauthException;
import com.devbliss.gpullr.service.dto.GithubOauthAccessToken;
import com.devbliss.gpullr.service.dto.GithubUser;
import com.devbliss.gpullr.util.http.JsonHttpClient;
import com.devbliss.gpullr.util.http.ValuePairList;
import com.devbliss.gpullr.util.http.ValuePairListFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Uni test for {@link GithubOauthService}
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubOauthServiceUnitTest {

  private static final String OAUTH_CLIENT_ID = "client_id";

  private static final String OAUTH_CLIENT_SECRET = "client_secret";

  private static final String OAUTH_CODE = "code";

  private static final String ENCODING_UTF_8 = "UTF-8";

  private GithubOauthService githubOauthService;

  @Mock
  private JsonHttpClient jsonHttpClient;

  @Mock
  private HttpPost httpPost;

  @Mock
  private HttpGet httpGet;

  @Mock
  private ValuePairListFactory valuePairListFactory;

  @Mock
  private HttpResponse httpResponse;

  @Mock
  private HttpEntity httpEntity;

  @Mock
  private StatusLine statusLine;

  @Mock
  private ValuePairList valuePairList;

  @Mock
  private UrlEncodedFormEntity urlEncodedFormEntity;

  @Mock
  private HttpClient httpClient;

  private GithubOauthAccessToken accessToken;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setUp() throws IOException {
    when(valuePairList.add(anyString(), anyString())).thenReturn(valuePairList);
    when(valuePairList.buildUrlEncoded()).thenReturn(urlEncodedFormEntity);

    when(jsonHttpClient.getHttpClient()).thenReturn(httpClient);
    when(jsonHttpClient.getPostMethod(anyString())).thenReturn(httpPost);
    when(jsonHttpClient.getGetMethod(anyString())).thenReturn(httpGet);

    when(valuePairListFactory.getNewValuePairList(anyInt())).thenReturn(valuePairList);

    accessToken = new GithubOauthAccessToken();
    accessToken.access_token = "some-random-access-token";
    when(httpResponse.getEntity()).thenReturn(httpEntity);

    when(httpResponse.getStatusLine()).thenReturn(statusLine);
    when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(httpClient.execute(any(HttpRequestBase.class))).thenReturn(httpResponse);

    githubOauthService = new GithubOauthService(new ObjectMapper(), jsonHttpClient, valuePairListFactory);
  }

  @Test
  public void testGetAccessTokenByCode() throws IOException {
    final String testCode = "some-random-test-code";

    final InputStream inputStream = IOUtils.toInputStream(objectMapper.writeValueAsString(accessToken), ENCODING_UTF_8);
    when(httpEntity.getContent()).thenReturn(inputStream);

    final GithubOauthAccessToken testAccessToken = githubOauthService.getAccessToken(testCode);

    verify(httpPost).setEntity(urlEncodedFormEntity);

    verify(valuePairList).add(eq(OAUTH_CLIENT_ID), anyString());
    verify(valuePairList).add(eq(OAUTH_CLIENT_SECRET), anyString());
    verify(valuePairList).add(OAUTH_CODE, testCode);

    assertEquals(accessToken.access_token, testAccessToken.access_token);
  }

  @Test(expected = OauthException.class)
  public void testGetAccessTokenWithUnsupportedEncoding() throws IOException {
    doThrow(new UnsupportedEncodingException()).when(valuePairList).buildUrlEncoded();
    githubOauthService.getAccessToken("some-random-test-code");
  }

  @Test(expected = OauthException.class)
  public void testGetAccessTokenWithIOException() throws IOException {
    doThrow(new IOException()).when(httpClient).execute(any(HttpRequestBase.class));
    githubOauthService.getAccessToken("some-random-test-code");
  }

  @Test(expected = OauthException.class)
  public void testGetAccessTokenGettingWrongResponseStatus() throws IOException {
    when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_NO_CONTENT);
    githubOauthService.getAccessToken("some-random-test-code");
  }

  @Test(expected = OauthException.class)
  public void testGetAccessTokenWithNull() {
    githubOauthService.getAccessToken(null);
  }

  @Test
  public void testGetUserByAccessToken() throws IOException {
    final GithubUser githubUser = new GithubUser();
    githubUser.id = 123456789;

    final InputStream inputStream = IOUtils.toInputStream(objectMapper.writeValueAsString(githubUser), ENCODING_UTF_8);
    when(httpEntity.getContent()).thenReturn(inputStream);

    final GithubUser testGithubUser = githubOauthService.getUserByAccessToken(accessToken);

    assertEquals(githubUser.id, testGithubUser.id);
  }

  @Test(expected = OauthException.class)
  public void testGetUserByAccessTokenWithIOException() throws IOException {
    doThrow(new IOException()).when(httpClient).execute(any(HttpRequestBase.class));
    githubOauthService.getUserByAccessToken(accessToken);
  }

  @Test(expected = OauthException.class)
  public void testGetUserByAccessTokenGettingWrongResponseStatus() throws IOException {
    when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_NO_CONTENT);
    githubOauthService.getUserByAccessToken(accessToken);
  }

  @Test(expected = OauthException.class)
  public void testGetUserByAccessTokenWithNull() {
    githubOauthService.getUserByAccessToken(null);
  }
}


package com.devbliss.gpullr.service;

import com.devbliss.gpullr.exception.OAuthException;
import com.devbliss.gpullr.service.dto.GithubOAuthAccessTokenDto;
import com.devbliss.gpullr.service.dto.GithubUserDto;
import com.devbliss.gpullr.util.http.JsonHttpClient;
import com.devbliss.gpullr.util.http.ValuePairList;
import com.devbliss.gpullr.util.http.ValuePairListFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This service wraps the GitHub OAuth API {@link https://developer.github.com/v3/oauth/}
 * and provides functions to follow the OAuth login web application flow.
 */
@Service
public class GithubOAuthService {

  private static final String GITHUB_OAUTH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

  private static final String GITHUB_API_GET_USER = "https://api.github.com/user";

  private static final String HTTP_HEADER_KEY_AUTHORIZATION = "Authorization";

  private static final String AUTHORIZATION_BY_TOKEN = "token %s";

  private static final String OAUTH_CLIENT_ID = "client_id";

  private static final String OAUTH_CLIENT_SECRET = "client_secret";

  private static final String OAUTH_CODE = "code";

  private static final String FAILED_HTTP_ERROR_CODE = "Communication to GitHub failed : HTTP error code : %d : %s";

  @Value("${github.client-id}")
  private String clientId;

  @Value("${github.client-secret}")
  private String clientSecret;

  private final ObjectMapper objectMapper;

  private final JsonHttpClient jsonHttpClient;

  private final ValuePairListFactory valuePairListFactory;

  @Autowired
  public GithubOAuthService(ObjectMapper objectMapper, JsonHttpClient jsonHttpClient,
      ValuePairListFactory valuePairListFactory) {
    this.objectMapper = objectMapper;
    this.jsonHttpClient = jsonHttpClient;
    this.valuePairListFactory = valuePairListFactory;
  }

  public GithubOAuthAccessTokenDto getAccessToken(String code) {
    if (code == null) {
      throw new OAuthException("Given code is NULL");
    }

    final CloseableHttpClient httpClient = jsonHttpClient.getHttpClient();
    final HttpPost postMethod = jsonHttpClient.getPostMethod(GITHUB_OAUTH_ACCESS_TOKEN_URL);

    final ValuePairList valuePairList = valuePairListFactory.getNewValuePairList(3)
        .add(OAUTH_CLIENT_ID, clientId)
        .add(OAUTH_CLIENT_SECRET, clientSecret)
        .add(OAUTH_CODE, code);

    try {
      postMethod.setEntity(valuePairList.buildUrlEncoded());
    } catch (UnsupportedEncodingException cause) {
      throw new OAuthException(cause);
    }

    try {

      return parseJsonResponseContentToObject(getValidResponseOk(httpClient, postMethod),
          GithubOAuthAccessTokenDto.class);

    } catch (IOException cause) {
      throw new OAuthException(cause);
    }

  }

  public GithubUserDto getUserByAccessToken(GithubOAuthAccessTokenDto oauthAccessToken) {
    if (oauthAccessToken == null) {
      throw new OAuthException("Given access token is NULL");
    }

    final CloseableHttpClient httpClient = jsonHttpClient.getHttpClient();
    final HttpGet getMethod = jsonHttpClient.getGetMethod(GITHUB_API_GET_USER);

    final String token = String.format(AUTHORIZATION_BY_TOKEN, oauthAccessToken.access_token);
    getMethod.setHeader(HTTP_HEADER_KEY_AUTHORIZATION, token);

    GithubUserDto githubUserDto;

    try {

      githubUserDto = parseJsonResponseContentToObject(getValidResponseOk(httpClient, getMethod), GithubUserDto.class);

    } catch (IOException cause) {

      throw new OAuthException(cause);

    } finally {

      try {
        httpClient.close();
      } catch (IOException cause) {
        throw new OAuthException(cause);
      }

    }

    return githubUserDto;
  }

  private HttpResponse getValidResponseOk(CloseableHttpClient httpClient, HttpRequestBase method) throws IOException {
    final HttpResponse response = httpClient.execute(method);

    if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
      throw new OAuthException(String.format(FAILED_HTTP_ERROR_CODE, response.getStatusLine().getStatusCode(),
          response.getStatusLine().getReasonPhrase()));
    }

    return response;
  }

  private <T> T parseJsonResponseContentToObject(HttpResponse response, Class<T> clazz) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

    String output;
    StringBuilder content = new StringBuilder();
    while ((output = br.readLine()) != null) {
      content.append(output);
    }

    return objectMapper.readValue(content.toString(), clazz);
  }

}

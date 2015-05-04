package com.devbliss.gpullr.service;

import com.devbliss.gpullr.exception.OauthException;
import com.devbliss.gpullr.service.dto.GithubOauthAccessToken;
import com.devbliss.gpullr.service.dto.GithubUser;
import com.devbliss.gpullr.util.http.JsonHttpClient;
import com.devbliss.gpullr.util.http.ValuePairList;
import com.devbliss.gpullr.util.http.ValuePairListFactory;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * This service wraps the Github Oauth API.
 */
@Service
public class GithubOauthService {

  private static final String GITHUB_OAUTH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
  private static final String GET_GITHUB_USER_URL = "https://api.github.com/user";
  private static final String HTTP_HEADER_KEY_AUTHORIZATION = "Authorization";
  private static final String AUTHORIZATION_BY_TOKEN = "token %s";
  private static final String OAUTH_CLIENT_ID = "client_id";
  private static final String OAUTH_CLIENT_SECRET = "client_secret";
  private static final String OAUTH_CODE = "code";
  private static final String FAILED_HTTP_ERROR_CODE = "Failed : HTTP error code : %d : %s";
  private static final int EXPECTED_RESPONSE_STATUS_CODE = 200;

  @Autowired
  Gson gson;

  @Autowired
  JsonHttpClient jsonHttpClient;

  @Autowired
  ValuePairListFactory valuePairListFactory;

  public GithubOauthAccessToken getAccessToken(String code) {
    final HttpClient httpClient = jsonHttpClient.getHttpClient();
    final HttpPost postMethod = jsonHttpClient.getPostMethod(GITHUB_OAUTH_ACCESS_TOKEN_URL);

    final ValuePairList valuePairList = valuePairListFactory.getNewValuePairList(3)
        .add(OAUTH_CLIENT_ID, "9c9a93c03eac2648bb3a")
        .add(OAUTH_CLIENT_SECRET, "e49d4368e87b19acc19279f720dcc6301dac9e51")
        .add(OAUTH_CODE, code);

    try {
      postMethod.setEntity(valuePairList.buildUrlEncoded());
    } catch (UnsupportedEncodingException cause) {
      throw new OauthException(cause);
    }

    try {

      return parseJsonResponseContentToObject(getValidResponse(httpClient, postMethod), GithubOauthAccessToken.class);

    } catch (IOException cause) {
      throw new OauthException(cause);
    }

  }

  public GithubUser getUserByAccessToken(GithubOauthAccessToken oauthAccessToken) {
    final HttpClient httpClient = jsonHttpClient.getHttpClient();
    final HttpGet getMethod = jsonHttpClient.getGetMethod(GET_GITHUB_USER_URL);

    final String token = String.format(AUTHORIZATION_BY_TOKEN, oauthAccessToken.access_token);
    getMethod.setHeader(HTTP_HEADER_KEY_AUTHORIZATION, token);

    try {

      return parseJsonResponseContentToObject(getValidResponse(httpClient, getMethod), GithubUser.class);

    } catch (IOException cause) {
      throw new OauthException(cause);
    }

  }

  private HttpResponse getValidResponse(HttpClient httpClient, HttpRequestBase method) throws IOException {
    final HttpResponse response = httpClient.execute(method);

    if (EXPECTED_RESPONSE_STATUS_CODE != response.getStatusLine().getStatusCode()) {
      throw new OauthException(String.format(FAILED_HTTP_ERROR_CODE, response.getStatusLine().getStatusCode(),
          response.getStatusLine().getReasonPhrase()));
    }

    return response;
  }

  private <T> T parseJsonResponseContentToObject(HttpResponse response, Class<T> clazz) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

    String output;
    String content = "";
    while ((output = br.readLine()) != null) {
      content += output;
    }

    return gson.fromJson(content, clazz);
  }

}

package com.devbliss.gpullr.service;

import com.devbliss.gpullr.service.dto.GithubOauthAccessToken;
import com.devbliss.gpullr.service.dto.GithubUser;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * This service wraps the GitHub Oauth API.
 */
@Service
public class OAuthService {

  public static final String GITHUB_OAUTH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
  public static final String GET_GITHUB_USER_URL = "https://api.github.com/user";

  @Autowired
  Gson gson;

  public GithubOauthAccessToken getAccessToken(String code) {
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost postMethod = new HttpPost(GITHUB_OAUTH_ACCESS_TOKEN_URL);
    postMethod.setHeader("Accept", "application/json");

    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
    nameValuePairs.add(new BasicNameValuePair("client_id", "9c9a93c03eac2648bb3a"));
    nameValuePairs.add(new BasicNameValuePair("client_secret", "e49d4368e87b19acc19279f720dcc6301dac9e51"));
    nameValuePairs.add(new BasicNameValuePair("code", code));

    try {
      postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    try {
      final HttpResponse response = httpClient.execute(postMethod);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

      String output;
      String content = "";
      while ((output = br.readLine()) != null) {
        content = output;
      }

      return gson.fromJson(content, GithubOauthAccessToken.class);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public GithubUser getUserByAccessToken(GithubOauthAccessToken oauthAccessToken) {
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet getMethod = new HttpGet(GET_GITHUB_USER_URL);
    getMethod.setHeader("Accept", "application/json");
    getMethod.setHeader("Authorization", "token " + oauthAccessToken.access_token);

    try {
      final HttpResponse response = httpClient.execute(getMethod);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

      String output;
      String content = "";
      while ((output = br.readLine()) != null) {
        content = output;
      }

      return gson.fromJson(content, GithubUser.class);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}

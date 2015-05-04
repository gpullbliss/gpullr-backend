package com.devbliss.gpullr.service.dto;

/**
 * DTO for JSON deserialization of {@link com.devbliss.gpullr.service.GithubOauthService#getAccessToken(String)} response.
 */
public class GithubOauthAccessToken {
  public String access_token;
  public String scope;
  public String token_type;
}

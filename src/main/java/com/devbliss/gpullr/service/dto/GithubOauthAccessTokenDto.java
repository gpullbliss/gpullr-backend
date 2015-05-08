package com.devbliss.gpullr.service.dto;

/**
 * DTO for JSON deserialization of
 * {@link com.devbliss.gpullr.service.GithubOAuthService#getAccessToken(String)} response.
 */
public class GithubOAuthAccessTokenDto {
  public String access_token;
}

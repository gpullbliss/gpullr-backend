package com.devbliss.gpullr.service.github;

import java.util.Optional;

/**
 * Fetches user details from eg. https://api.github.com/users/octocat
 */
public class GetUserDetailsRequest extends AbstractGithubRequest {

  private final String userDetailsUrl;

  public GetUserDetailsRequest(String userDetailsUrl) {
    super(Optional.empty());
    this.userDetailsUrl = userDetailsUrl;
  }

  @Override
  protected String createUri(int page) {
    return userDetailsUrl;
  }
}

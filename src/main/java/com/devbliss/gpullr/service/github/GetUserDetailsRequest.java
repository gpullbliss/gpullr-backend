package com.devbliss.gpullr.service.github;

import java.util.Optional;

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

package com.devbliss.gpullr.service.github;

public class GetUserDetailsRequest extends AbstractGithubRequest {

  private final String userDetailsUrl;

  public GetUserDetailsRequest(String userDetailsUrl) {
    super(null);
    this.userDetailsUrl = userDetailsUrl;
  }

  @Override
  protected String createUri(int page) {
    return userDetailsUrl;
  }
}

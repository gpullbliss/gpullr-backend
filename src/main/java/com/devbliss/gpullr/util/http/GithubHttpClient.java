package com.devbliss.gpullr.util.http;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Performs HTTP calls against the GitHub API.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface GithubHttpClient {

  GithubHttpResponse execute(HttpUriRequest request);
}

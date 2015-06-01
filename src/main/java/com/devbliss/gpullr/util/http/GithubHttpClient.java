package com.devbliss.gpullr.util.http;

import com.devbliss.gpullr.service.github.AbstractGithubRequest;

/**
 * Performs (synchronous) HTTP calls against the GitHub API.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface GithubHttpClient {

  GithubHttpResponse execute(AbstractGithubRequest request);
}

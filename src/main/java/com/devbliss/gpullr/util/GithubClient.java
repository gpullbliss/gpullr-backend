package com.devbliss.gpullr.util;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Wraps a generic HTTP client and provides a method to make http calls.
 * Can be used e.g. for intercepting every requests with default headers or doing other configuration stuff.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface GithubClient {

  GithubHttpResponse execute(HttpUriRequest request);
}

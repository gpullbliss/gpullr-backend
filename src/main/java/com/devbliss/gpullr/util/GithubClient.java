package com.devbliss.gpullr.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Our version of GitHub client. Automatically
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface GithubClient {

  HttpResponse execute(HttpUriRequest request);
}

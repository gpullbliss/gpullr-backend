package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.BuildStatus;
import com.devbliss.gpullr.util.http.GithubHttpResponse;
import java.util.List;

/**
 * Response to a request that has fetched the CI build statuses for a certain branch.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubPullRequestBuildStatusResponse extends AbstractGithubResponse<List<BuildStatus>> {

  public GithubPullRequestBuildStatusResponse(
      List<BuildStatus> payload, GithubHttpResponse resp) {
    super(payload, resp);
  }
}

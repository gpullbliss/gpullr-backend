package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.BuildStatus;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.util.http.GithubHttpResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import org.springframework.stereotype.Component;

/**
 * Parses JSON payload from a {@link GithubPullRequestBuildStatusResponse} to (lists of) 
 * {@link BuildStatus} objects. 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class PullRequestBuildStatusParser {

  private static final String FIELD_KEY_TIMESTAMP = "created_at";
  private static final String FIELD_KEY_STATE = "state";
  private static final String ERR = "Github-Response for pullrequest build statuses did not return list of json objects, pull request was '%s'";

  public GithubPullRequestBuildStatusResponse parse(GithubHttpResponse resp, PullRequest pullRequest) {
    List<JsonObject> jsonObjects = resp.jsonObjects
      .orElseThrow(() -> new UnexpectedException(String.format(ERR, pullRequest.title)));
    List<BuildStatus> buildStates = jsonObjects.stream().map(this::parse).collect(Collectors.toList());
    return new GithubPullRequestBuildStatusResponse(buildStates, resp);
  }

  private BuildStatus parse(JsonObject jsonObject) {
    BuildStatus pullRequestBuildStatus = new BuildStatus();
    pullRequestBuildStatus.timestamp = ZonedDateTime.parse(jsonObject.getString(FIELD_KEY_TIMESTAMP));
    pullRequestBuildStatus.state = BuildStatus.State.parse(jsonObject.getString(FIELD_KEY_STATE));
    return pullRequestBuildStatus;
  }
}

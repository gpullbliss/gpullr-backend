package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.BuildStatus;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.util.http.GithubHttpResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestBuildStatusParser.class);

  private static final String FIELD_KEY_TIMESTAMP = "created_at";

  private static final String FIELD_KEY_STATE = "state";

  private static final String ERR =
      "Github-Response for pullrequest build statuses did not return list of json objects, pull request was '%s'";

  public GithubPullRequestBuildStatusResponse parse(GithubHttpResponse resp, String pullRequestTitle) {

    List<BuildStatus> buildStates;

    if (resp.getStatusCode() == HttpStatus.SC_OK) {
      List<JsonObject> jsonObjects = resp.getJsonObjects()
        .orElseThrow(() -> new UnexpectedException(String.format(ERR, pullRequestTitle)));
      buildStates = jsonObjects.stream().map(this::parse).collect(Collectors.toList());
    } else if (resp.getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
      buildStates = new ArrayList<>();
      LOGGER.debug("CI build status not modified for pull request '{}'", pullRequestTitle);
    } else {
      LOGGER.debug(
          "Unexpected response status {} when fetching build status of pull request '{}'",
          resp.getStatusCode(),
          pullRequestTitle);
      buildStates = new ArrayList<>();
    }

    return new GithubPullRequestBuildStatusResponse(buildStates, resp);
  }

  private BuildStatus parse(JsonObject jsonObject) {
    BuildStatus pullRequestBuildStatus = new BuildStatus();
    pullRequestBuildStatus.timestamp = ZonedDateTime.parse(jsonObject.getString(FIELD_KEY_TIMESTAMP));
    pullRequestBuildStatus.state = BuildStatus.State.parse(jsonObject.getString(FIELD_KEY_STATE));
    return pullRequestBuildStatus;
  }
}

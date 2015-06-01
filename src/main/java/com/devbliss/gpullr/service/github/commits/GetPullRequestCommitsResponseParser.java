package com.devbliss.gpullr.service.github.commits;

import com.devbliss.gpullr.domain.Commit;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.util.http.GithubHttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetPullRequestCommitsResponseParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetPullRequestCommitsResponseParser.class);

  private static final String JSON_FIELD_KEY_SHA = "sha";

  private static final String ERR =
      "Github-Response for commits did not return list of json objects, pull request was '%s'";

  public List<Commit> parse(GithubHttpResponse resp, String pullRequestTitle) {

    if (resp.getStatusCode() == HttpStatus.SC_OK) {
      return resp
        .getJsonObjects()
        .orElseThrow(() -> new UnexpectedException(String.format(ERR, pullRequestTitle)))
        .stream()
        .map(this::parseCommit)
        .collect(Collectors.toList());
    } else if (resp.getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
      LOGGER.debug("Commits not modified for pull request '{}'", pullRequestTitle);
    } else {
      LOGGER.warn(
          "Unexpected response status {} when fetching commits of pull request '{}'",
          resp.getStatusCode(),
          pullRequestTitle);
    }

    return new ArrayList<>();
  }

  private Commit parseCommit(JsonObject commitPayload) {
    String sha = commitPayload.getString(JSON_FIELD_KEY_SHA);
    return new Commit(sha);
  }
}
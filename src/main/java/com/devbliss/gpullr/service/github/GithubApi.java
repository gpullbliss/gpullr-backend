package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.PullRequestEvent.Action;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.util.Log;
import com.devbliss.gpullr.util.http.GithubHttpClient;
import com.devbliss.gpullr.util.http.GithubHttpResponse;
import com.jcabi.github.Github;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Wrapper for GitHub API, facading the library used for the API calls.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class GithubApi {

  private static final String EVENT_TYPE_PULL_REQUEST = "PullRequestEvent";

  private static final String HEADER_LINK = "Link";

  private static final String FIELD_KEY_ID = "id";

  public static final String FIELD_KEY_LOGIN = "login";

  private static final String HEADER_MARKER_MORE_PAGES = "next";

  private static final String FIELD_KEY_NAME = "name";
  
  private static final String FIELD_KEY_AVATAR_URL = "avatar_url";
  
  private static final String FIELD_KEY_PROFILE_URL = "html_url";
  
  private static final String FIELD_KEY_PULLREQUEST_URL = "html_url";

  private static final String FIELD_KEY_DESCRIPTION = "description";

  private static final String FIELD_KEY_PAYLOAD = "payload";

  private static final String FIELD_KEY_TYPE = "type";

  private static final String FIELD_KEY_ACTION = "action";

  private static final String FIELD_KEY_ASSIGNEE = "assignee";

  private static final String FIELD_KEY_CLOSED_AT = "closed_at";

  private static final String FIELD_KEY_MERGED_AT = "merged_at";

  private static final String ERR_MSG_RESPONSE = "Request to '%s' returned unexpected status code: %d.";

  @Log
  private Logger logger;

  @Autowired
  private Github client;

  @Autowired
  private GithubHttpClient githubClient;

  /**
   * Retrieves all repositories (public, private, forked, etc.) belonging to our organization, from GitHub.
   *
   * @return possibly empty list of repositories
   */
  public List<Repo> fetchAllGithubRepos() throws UnexpectedException {
    try {
      return loadAllPages("/orgs/devbliss/repos",
        jo -> new Repo(jo.getInt(FIELD_KEY_ID), jo.getString(FIELD_KEY_NAME), jo.getString(FIELD_KEY_DESCRIPTION)));
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  /**
   * Fetches data of an existing pull request.
   *
   * @param pullRequest
   * @param etagHeader
   * @return response object containing the actual pull request plus response meta data required for next request
   */
  public GithubPullRequestResponse fetchPullRequest(PullRequest pullRequest, Optional<String> etagHeader) {
    GetPullRequestDetailsRequest req = new GetPullRequestDetailsRequest(pullRequest, etagHeader);
    GithubHttpResponse resp = githubClient.execute(req);

    try {
      Optional<PullRequest> fetchedPullRequest = handleResponse(resp, this::parsePullRequestPayload);
      return new GithubPullRequestResponse(fetchedPullRequest, resp.getNextFetch(), resp.getEtag());
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public GithubEventsResponse fetchAllEvents(Repo repo, Optional<String> etagHeader) {

    try {
      GetGithubEventsRequest req = new GetGithubEventsRequest(repo, etagHeader, 0);
      GithubHttpResponse resp = githubClient.execute(req);
      List<PullRequestEvent> events = new ArrayList<>();
      Optional<String> etag = resp.getEtag();
      Instant nextFetch = resp.getNextFetch();
      GithubEventsResponse result = new GithubEventsResponse(events, nextFetch, etag);
      handleResponse(resp, jo -> parseEvent(jo, repo), req.requestForNextPage()).forEach(
        ope -> ope.ifPresent(result.payload::add));
      return result;
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public List<User> fetchAllOrgaMembers() throws IOException {
    return loadAllPages("/orgs/devbliss/members", this::getUserWithDetailsFromJson);
  }

  public void assignUserToPullRequest(User user, PullRequest pull) {
    JsonObject json = Json.createObjectBuilder().add(FIELD_KEY_ASSIGNEE, user.username).build();
    final String uri = buildIssueUri(pull.repo.name, pull.number);

    try {
      Request req = client.entry()
        .method(Request.PATCH).body().set(json)
        .back().uri().path(uri)
        .back();

      req.fetch();

    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public void unassignUserFromPullRequest(User user, PullRequest pull) {
    JsonObject json = Json.createObjectBuilder().add(FIELD_KEY_ASSIGNEE, "").build();
    final String uri = buildIssueUri(pull.repo.name, pull.number);

    try {
      Request req = client.entry()
          .method(Request.PATCH).body().set(json)
          .back().uri().path(uri)
          .back();

      req.fetch();

    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  private User parseUser(JsonObject userJson) {
    return new User(userJson.getInt(FIELD_KEY_ID), userJson.getString(FIELD_KEY_LOGIN));
  }

  private User getUserWithDetailsFromJson(JsonObject userJson) {
    GetUserDetailsRequest req = new GetUserDetailsRequest(userJson.getString("url"));
    GithubHttpResponse resp = githubClient.execute(req);
    try {
      Optional<User> user = handleResponse(resp, this::parseUserDetails);
      return user.orElseThrow(() -> new UnexpectedException(
          "User details could not be found for user '" + userJson.getString(FIELD_KEY_LOGIN) + "'."));
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  private User parseUserDetails(JsonObject userJson) {
    return new User(
        userJson.getInt(FIELD_KEY_ID),
        userJson.getString(FIELD_KEY_LOGIN),
        userJson.getString(FIELD_KEY_NAME, ""),
        userJson.getString(FIELD_KEY_AVATAR_URL),
        userJson.getString(FIELD_KEY_PROFILE_URL)
    );
  }

  private Optional<PullRequestEvent> parseEvent(JsonObject eventJson, Repo repo) {
    if (isPullRequestEvent(eventJson)) {
      return parsePullRequestEvent(eventJson, repo);
    }
    return Optional.empty();
  }

  private Optional<PullRequestEvent> parsePullRequestEvent(JsonObject eventJson, Repo repo) {
    JsonObject payloadJson = eventJson.getJsonObject(FIELD_KEY_PAYLOAD);
    Action action = Action.parse(payloadJson.getString(FIELD_KEY_ACTION));
    PullRequest pullRequest = parsePullRequestPayload(payloadJson.getJsonObject("pull_request"));
    pullRequest.repo = repo;
    return Optional.of(new PullRequestEvent(action, pullRequest));
  }

  private PullRequest parsePullRequestPayload(JsonObject pullRequestJson) {

    PullRequest pullRequest = new PullRequest();
    pullRequest.id = pullRequestJson.getInt(FIELD_KEY_ID);
    pullRequest.url = pullRequestJson.getString(FIELD_KEY_PULLREQUEST_URL);
    pullRequest.title = pullRequestJson.getString("title");
    pullRequest.createdAt = ZonedDateTime.parse(pullRequestJson.getString("created_at"));
    pullRequest.author = parseUser(pullRequestJson.getJsonObject("user"));
    pullRequest.linesAdded = pullRequestJson.getInt("additions");
    pullRequest.linesRemoved = pullRequestJson.getInt("deletions");
    pullRequest.filesChanged = pullRequestJson.getInt("changed_files");
    pullRequest.number = pullRequestJson.getInt("number");
    JsonValue assigneeValue = pullRequestJson.get(FIELD_KEY_ASSIGNEE);

    // unfortunately, assignee is only set when PR is CLOSED - so it's useless for us!
    if (assigneeValue.getValueType() == ValueType.OBJECT) {
      pullRequest.assignee = parseUser((JsonObject) assigneeValue);
    }

    if (isStringValue(pullRequestJson, FIELD_KEY_CLOSED_AT)) {
      pullRequest.closedAt = ZonedDateTime.parse(pullRequestJson.getString(FIELD_KEY_CLOSED_AT));
      logger.debug("parsed closed-date of PR event: {} in pr {}", pullRequest.closedAt, pullRequest.url);
    } else if (isStringValue(pullRequestJson, FIELD_KEY_MERGED_AT)) {
      pullRequest.closedAt = ZonedDateTime.parse(pullRequestJson.getString(FIELD_KEY_MERGED_AT));
      logger.debug("parsed merged-date of PR event: {} in pr {}", pullRequest.closedAt, pullRequest.url);
    } else {
      logger.debug("Neither close-date nor merged-date found in pull request payload of " + pullRequest.url);
    }

    return pullRequest;
  }

  private boolean isStringValue(JsonObject jsonObject, String fieldKey) {
    return jsonObject.containsKey(fieldKey) && jsonObject.get(fieldKey).getValueType() == ValueType.STRING;
  }

  private boolean isPullRequestEvent(JsonObject event) {
    return EVENT_TYPE_PULL_REQUEST.equals(event.getString(FIELD_KEY_TYPE));
  }

  private <T> List<T> loadAllPages(String path, Function<JsonObject, T> mapper) throws IOException {
    final JsonResponse resp = client.entry().uri().path(path).back().fetch().as(JsonResponse.class);
    return handleResponse(resp, mapper, path, 1);
  }

  private <T> List<T> handleResponse(GithubHttpResponse resp, Function<JsonObject, T> mapper,
    GetGithubEventsRequest nextRequest) throws IOException {

    int statusCode = resp.statusCode;

    if (statusCode == org.apache.http.HttpStatus.SC_OK) {
      List<T> result = responseToList(resp, mapper);

      if (hasMorePages(resp)) {
        resp = githubClient.execute(nextRequest);
        result.addAll(handleResponse(resp, mapper, nextRequest.requestForNextPage()));
      }

      return result;
    } else if (statusCode == org.apache.http.HttpStatus.SC_NOT_MODIFIED) {
      return new ArrayList<>();
    } else {
      logger.warn(String.format(ERR_MSG_RESPONSE, resp.uri, statusCode));
      return new ArrayList<>();
    }
  }

  private <T> Optional<T> handleResponse(GithubHttpResponse resp, Function<JsonObject, T> mapper) throws IOException {

    int statusCode = resp.statusCode;

    if (statusCode == org.apache.http.HttpStatus.SC_OK) {
      return Optional.of(mapper.apply(resp.jsonObject.get()));
    } else if (statusCode == org.apache.http.HttpStatus.SC_NOT_MODIFIED) {
      return Optional.empty();
    } else {
      throw new UnexpectedException(String.format(ERR_MSG_RESPONSE, resp.uri, statusCode));
    }
  }

  private <T> List<T> handleResponse(JsonResponse resp, Function<JsonObject, T> mapper, String path, int page)
    throws IOException {

    List<T> result = responseToList(resp, mapper);

    if (hasMorePages(resp)) {
      resp = client.entry().uri().path(path).queryParam("page", page).back().fetch().as(JsonResponse.class);
      result.addAll(handleResponse(resp, mapper, path, page + 1));
    }
    return result;
  }

  private boolean hasMorePages(JsonResponse resp) {
    return resp.headers().keySet().contains(HEADER_LINK)
      && resp.headers().get(HEADER_LINK).stream().anyMatch(s -> s.contains(HEADER_MARKER_MORE_PAGES));
  }

  private boolean hasMorePages(GithubHttpResponse resp) {
    String linkHeader = resp.headers.get(HEADER_LINK);
    return linkHeader != null && linkHeader.contains(HEADER_MARKER_MORE_PAGES);
  }

  private <T> List<T> responseToList(GithubHttpResponse resp, Function<JsonObject, T> mapper) {

    try {
      return resp.jsonObjects.get()
          .stream()
          .map(mapper)
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new UnexpectedException(e);
    }
  }

  private <T> List<T> responseToList(JsonResponse resp, Function<JsonObject, T> mapper) {
    // JsonResponse#json() fails on non-UTF-8 characters, so we have to do the binary workaround:
    JsonReaderFactory jrf = Json.createReaderFactory(null);

    try {
      return jrf.createReader(new ByteArrayInputStream(resp.binary()))
          .readArray()
          .stream()
          .filter(v -> v.getValueType() == ValueType.OBJECT)
          .map(v -> (JsonObject) v)
          .map(mapper)
          .collect(Collectors.toList());
    } catch (JsonException e) {
      throw new UnexpectedException(e);
    }
  }

  private String buildIssueUri(final String repoName, final int pullNumber) {
    return "/repos/devbliss/" + repoName + "/issues/" + pullNumber;
  }

}

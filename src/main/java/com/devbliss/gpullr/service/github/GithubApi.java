package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.PullRequestEvent.Action;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.util.GithubClient;
import com.devbliss.gpullr.util.Log;
import com.jcabi.github.Github;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue.ValueType;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
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

  private static final String HEADER_POLL_INTERVAL = "X-Poll-Interval";

  private static final String HEADER_ETAG = "ETag";

  private static final String HEADER_LINK = "Link";

  private static final String FIELD_KEY_ID = "id";

  private static final String FIELD_KEY_NAME = "name";

  private static final String FIELD_KEY_DESCRIPTION = "description";

  private static final String FIELD_KEY_PAYLOAD = "payload";

  private static final String FIELD_KEY_TYPE = "type";

  private static final String FIELD_KEY_ACTION = "action";
  
  private static final int DEFAULT_POLL_INTERVAL = 60;

  @Log
  private Logger logger;

  @Autowired
  private Github client;

  @Autowired
  private GithubClient githubClient;

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

  public GithubEventsResponse fetchAllEvents(Repo repo, Optional<String> etagHeader) {

    try {
      GetGithubEventsRequest req = new GetGithubEventsRequest(repo, etagHeader, 0);
      HttpResponse resp = githubClient.execute(req);
      List<PullRequestEvent> events = new ArrayList<>();
      Optional<String> etag = getEtag(resp);
      int nextRequestAfterSeconds = getPollInterval(resp);
      GithubEventsResponse result = new GithubEventsResponse(events, nextRequestAfterSeconds, etag);
      handleResponse(resp, jo -> parseEvent(jo, repo), req.nextPage()).forEach(
          ope -> ope.ifPresent(result.pullRequestEvents::add));
      return result;
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public List<User> fetchAllOrgaMembers() throws IOException {
    return loadAllPages("/orgs/devbliss/members", this::parseUser);
  }

  public void assignUserToPullRequest(User user, PullRequest pull) {
    JsonObject json = Json.createObjectBuilder().add("assignee", user.username).build();
    final String uri = "/repos/devbliss/" + pull.repo.name + "/issues/" + pull.number;

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
    return new User(userJson.getInt(FIELD_KEY_ID), userJson.getString("login"), userJson.getString("avatar_url"));
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
    pullRequest.url = pullRequestJson.getString("html_url");
    pullRequest.title = pullRequestJson.getString("title");
    pullRequest.createdAt = ZonedDateTime.parse(pullRequestJson.getString("created_at"));
    pullRequest.owner = parseUser(pullRequestJson.getJsonObject("user"));
    pullRequest.additions = pullRequestJson.getInt("additions");
    pullRequest.deletions = pullRequestJson.getInt("deletions");
    pullRequest.changedFiles = pullRequestJson.getInt("changed_files");
    pullRequest.number = pullRequestJson.getInt("number");
    return pullRequest;
  }

  private boolean isPullRequestEvent(JsonObject event) {
    return EVENT_TYPE_PULL_REQUEST.equals(event.getString(FIELD_KEY_TYPE));
  }

  private <T> List<T> loadAllPages(String path, Function<JsonObject, T> mapper) throws IOException {
    final JsonResponse resp = client.entry().uri().path(path).back().fetch().as(JsonResponse.class);
    return handleResponse(resp, mapper, path, 1);
  }

  // private GithubEventsResponse handleGithubEventsResponse(JsonResponse resp,
  // Function<JsonObject, Optional<PullRequestEvent>> mapper,
  // String path, int page)
  // throws IOException {
  //
  // List<PullRequestEvent> events = new ArrayList<>();
  // Optional<String> etag = getEtag(resp);
  // int nextRequestAfterSeconds = getPollInterval(resp);
  // GithubEventsResponse result = new GithubEventsResponse(events, nextRequestAfterSeconds, etag);
  // handleResponse(resp, mapper, path, page + 1).forEach(ope ->
  // ope.ifPresent(result.pullRequestEvents::add));
  // return result;
  // }
  //
  // private GithubEventsResponse handleGithubEventsResponse(HttpResponse resp,
  // Function<JsonObject, Optional<PullRequestEvent>> mapper,
  // GetGithubEventsRequest followUpRequest)
  // throws IOException {
  //
  // List<PullRequestEvent> events = new ArrayList<>();
  // Optional<String> etag = getEtag(resp);
  // int nextRequestAfterSeconds = getPollInterval(resp);
  // GithubEventsResponse result = new GithubEventsResponse(events, nextRequestAfterSeconds, etag);
  // handleResponse(resp, mapper, followUpRequest.nextPage()).forEach(
  // ope -> ope.ifPresent(result.pullRequestEvents::add));
  // return result;
  // }

  private Optional<String> getEtag(JsonResponse resp) {
    if (resp.headers().containsKey(HEADER_ETAG)) {
      return Optional.of(resp.headers().get(HEADER_ETAG).get(0));
    } else {
      return Optional.empty();
    }
  }

  private Optional<String> getEtag(HttpResponse resp) {
    Header[] etagHeaders = resp.getHeaders(HEADER_ETAG);

    if (etagHeaders != null && etagHeaders.length > 0) {
      return Optional.of(etagHeaders[0].getValue());
    } else {
      return Optional.empty();
    }
  }

  private int getPollInterval(JsonResponse resp) {
    if (resp.headers().get(HEADER_POLL_INTERVAL) == null) {
      logger.debug("No poll interval header set in response, using default = " + DEFAULT_POLL_INTERVAL);
      return DEFAULT_POLL_INTERVAL;
    }

    return Integer.parseInt(resp.headers().get(HEADER_POLL_INTERVAL).get(0));
  }

  private int getPollInterval(HttpResponse resp) {
    Header[] pollIntervalHeaders = resp.getHeaders(HEADER_POLL_INTERVAL);

    if (pollIntervalHeaders == null || pollIntervalHeaders.length == 0) {
      logger.debug("No poll interval header set in response, using default = " + DEFAULT_POLL_INTERVAL);
      return DEFAULT_POLL_INTERVAL;
    }

    return Integer.parseInt(pollIntervalHeaders[0].getValue());
  }

  private <T> List<T> handleResponse(HttpResponse resp, Function<JsonObject, T> mapper,
      GetGithubEventsRequest nextRequest) throws IOException {

    int statusCode = resp.getStatusLine().getStatusCode();
    logger.debug("*** got http status: " + statusCode);

    if (statusCode == org.apache.http.HttpStatus.SC_OK) {
      List<T> result = responseToList(resp, mapper);

      if (hasMorePage(resp)) {
        logger.debug("******* has more pages");
        resp = githubClient.execute(nextRequest);
        result.addAll(handleResponse(resp, mapper, nextRequest.nextPage()));
      }

      return result;
    }

    else if (statusCode == org.apache.http.HttpStatus.SC_NOT_MODIFIED) {
      return new ArrayList<>();
    }

    else {
      throw new UnexpectedException("Fetching events from GitHub returned status code " + statusCode);
    }
  }

  private <T> List<T> handleResponse(JsonResponse resp, Function<JsonObject, T> mapper, String path, int page)
      throws IOException {

    List<T> result = responseToList(resp, mapper);

    if (hasMorePage(resp)) {
      resp = client.entry().uri().path(path).queryParam("page", page).back().fetch().as(JsonResponse.class);
      result.addAll(handleResponse(resp, mapper, path, page + 1));
    }
    return result;
  }

  private boolean hasMorePage(JsonResponse resp) {
    return resp.headers().keySet().contains(HEADER_LINK)
        && resp.headers().get(HEADER_LINK).stream().anyMatch(s -> s.contains("next"));
  }

  private boolean hasMorePage(HttpResponse resp) {
    Header[] linkHeader = resp.getHeaders(HEADER_LINK);

    if (linkHeader != null && linkHeader.length > 0) {
      return Stream.of(linkHeader).filter(h -> h.getValue().contains("next")).findAny().isPresent();
    }

    return false;
  }

  private <T> List<T> responseToList(HttpResponse resp, Function<JsonObject, T> mapper) {
    JsonReaderFactory jrf = Json.createReaderFactory(null);

    try {
      return jrf.createReader(resp.getEntity().getContent())
        .readArray()
        .stream()
        .filter(v -> v.getValueType() == ValueType.OBJECT)
        .map(v -> (JsonObject) v)
        .map(mapper)
        .collect(Collectors.toList());
    } catch (IOException e) {
      throw new UnexpectedException(e);
    } catch (JsonException e) {
      logger.error("Error reading response json: " + e.getMessage());
      logger.error("raw response:");
      logger.error(resp.toString());
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
      logger.error("Error reading response json: " + e.getMessage());
      logger.error("raw response:");
      logger.error(resp.toString());
      throw e;
    }
  }
}

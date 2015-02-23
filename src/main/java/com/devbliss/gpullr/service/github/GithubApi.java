package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.PullRequestEvent.Action;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.util.Log;
import com.jcabi.github.Github;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.response.JsonResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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

  private static final String HEADER_POLL_INTERVAL = "X-Poll-Interval";

  private static final String HEADER_ETAG = "ETag";

  private static final String FIELD_KEY_ID = "id";

  private static final String FIELD_KEY_NAME = "name";

  private static final String FIELD_KEY_DESCRIPTION = "description";

  private static final String FIELD_KEY_PAYLOAD = "payload";

  private static final String HEADER_LINK = "Link";

  private static final String FIELD_KEY_TYPE = "type";

  private static final String FIELD_KEY_ACTION = "action";

  @Log
  private Logger logger;

  @Autowired
  private Github client;

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
      String path = "repos/devbliss/" + repo.name + "/events";
      Request req = client.entry().uri().path(path).back();

      if (etagHeader.isPresent()) {
        req.header(HEADER_ETAG, etagHeader.get());
        logger.debug("******** ETAG HEADER PRESENT: " + etagHeader.get());
      } else {
        logger.debug("******** ETAG HEADER __NOT__ PRESENT!");
      }

      final JsonResponse resp = req.fetch().as(JsonResponse.class);
      return handleGithubEventsResponse(resp, jo -> parseEvent(jo, repo), path, 1);
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public List<User> fetchAllOrgaMembers() throws IOException {
    return loadAllPages("/orgs/devbliss/members", this::parseUser);
  }

  public void assingUserToPullRequest(User user, PullRequest pull) {
    JsonObject json = Json.createObjectBuilder().add("assignee", user.username).build();
    final String uri = "/repos/devbliss/" + pull.repo.name + "/issues/" + pull.number;

    try {
      Request req = client.entry()
        .method(Request.PATCH).body().set(json)
        .back().uri().path(uri)
        .back();

      Response fetch = req.fetch();
      System.out.println(fetch);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private User parseUser(JsonObject userJson) {
    return new User(userJson.getInt(FIELD_KEY_ID), userJson.getString("login"), userJson.getString("avatar_url"));
  }

  private Optional<PullRequestEvent> parseEvent(JsonObject eventJson, Repo repo) {
    if (isPullRequestEvent(eventJson)) {
      return parsePullrequestEvent(eventJson, repo);
    }
    return Optional.empty();
  }

  private Optional<PullRequestEvent> parsePullrequestEvent(JsonObject eventJson, Repo repo) {
    JsonObject payloadJson = eventJson.getJsonObject(FIELD_KEY_PAYLOAD);
    Action action = Action.parse(payloadJson.getString(FIELD_KEY_ACTION));
    PullRequest pullrequest = parsePullrequestPayload(payloadJson.getJsonObject("pull_request"));
    pullrequest.repo = repo;
    return Optional.of(new PullRequestEvent(action, pullrequest));
  }

  private PullRequest parsePullrequestPayload(JsonObject pullrequestJson) {

    PullRequest pullRequest = new PullRequest();
    pullRequest.id = pullrequestJson.getInt(FIELD_KEY_ID);
    pullRequest.url = pullrequestJson.getString("html_url");
    pullRequest.title = pullrequestJson.getString("title");
    pullRequest.createdAt = ZonedDateTime.parse(pullrequestJson.getString("created_at"));
    pullRequest.owner = parseUser(pullrequestJson.getJsonObject("user"));
    pullRequest.additions = pullrequestJson.getInt("additions");
    pullRequest.deletions = pullrequestJson.getInt("deletions");
    pullRequest.changedFiles = pullrequestJson.getInt("changed_files");
    pullRequest.number = pullrequestJson.getInt("number");
    return pullRequest;
  }

  private boolean isPullRequestEvent(JsonObject event) {
    return EVENT_TYPE_PULL_REQUEST.equals(event.getString(FIELD_KEY_TYPE));
  }

  private <T> List<T> loadAllPages(String path, Function<JsonObject, T> mapper) throws IOException {
    final JsonResponse resp = client.entry().uri().path(path).back().fetch().as(JsonResponse.class);
    return handleResponse(resp, mapper, path, 1);
  }

  private GithubEventsResponse handleGithubEventsResponse(JsonResponse resp,
      Function<JsonObject, Optional<PullRequestEvent>> mapper,
      String path, int page)
      throws IOException {

    List<PullRequestEvent> events = new ArrayList<>();
    Optional<String> etag = getEtag(resp);
    int nextRequestAfterSeconds = getPollInterval(resp);
    GithubEventsResponse result = new GithubEventsResponse(events, nextRequestAfterSeconds, etag);
    handleResponse(resp, mapper, path, page + 1).forEach(ope -> ope.ifPresent(result.pullRequestEvents::add));
    return result;
  }

  private Optional<String> getEtag(JsonResponse resp) {
    if (resp.headers().containsKey(HEADER_ETAG)) {
      return Optional.of(resp.headers().get(HEADER_ETAG).get(0));
    } else {
      return Optional.empty();
    }
  }

  private int getPollInterval(JsonResponse resp) {
    if (resp.headers().get(HEADER_POLL_INTERVAL) == null) {
      throw new UnexpectedException("No poll interval header set in response, response state was: "
          + resp.status() + " / " + resp.reason());
    }

    return Integer.parseInt(resp.headers().get(HEADER_POLL_INTERVAL).get(0));
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

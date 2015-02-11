package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.GithubEvent;
import com.devbliss.gpullr.domain.GithubEvent.Type;
import com.devbliss.gpullr.domain.GithubEventsResponse;
import com.devbliss.gpullr.domain.GithubPullrequestEvent;
import com.devbliss.gpullr.domain.GithubRepo;
import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.Pullrequest.State;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.jcabi.github.Github;
import com.jcabi.http.response.JsonResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Wrapper for GitHub API, facading the library used for the API calls.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class GithubApi {

  private static final String EVENT_TYPE_CREATE = "CreateEvent";
  private static final String EVENT_SUBTYPE_REPO = "repository";
  private static final String EVENT_TYPE_PULL_REQUEST = "PullRequestEvent";
  private static final String PULLREQUEST_ACTION_CREATED = "opened";

  @Autowired
  private Github client;

  /**
   * Retrieves all repositories (public, private, forked, etc.) belonging to our organization, from GitHub.
   * 
   * @return possibly empty list of repositories
   */
  public List<GithubRepo> fetchAllGithubRepos() throws UnexpectedException {
    try {
      return loadAllPages("/orgs/devbliss/repos",
          jo -> new GithubRepo(jo.getInt("id"), jo.getString("name"), jo.getString("description")));
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  // 2571083784: CreateEvent: 2015-02-10T14:32:56Z:
  // {"ref":"feature/add_global_jshint_rules","ref_type":"branch","master_branch":"master","description":"global ruleset for the ecosystem","pusher_type":"user"}
  public GithubEventsResponse fetchAllEvents(String repoName) throws IOException {

    loadAllPages("repos/devbliss/" + repoName + "/events", jo -> parseEvent(jo)).forEach(s ->
        System.out.println(s));

    // WORKS:
    /*

     */
    loadAllPages(
        "orgs/devbliss/events",
        jo -> {
          handleEvent(jo);
          return "";
        });
    // jo -> jo.getString("id") + ": " + jo.getString("type") + ": " + jo.getString("created_at") +
    // ": "
    // + (jo.containsKey("payload") ? jo.get("payload") : "-")).forEach(
    // s -> {
    // System.out.println("\r\n\r\n\r\n***\r\n\r\n");
    // System.out.println(s);
    // System.out.println("\r\n\r\n\r\n***\r\n\r\n");

    // });
    return null;
  }

  private Optional<? extends GithubEvent<?>> parseEvent(JsonObject jsonObject) {
    if (isPullRequestCreatedEvent(jsonObject)) {
      return parsePullrequestEvent(jsonObject);
    }
    return Optional.empty();
  }

  private Optional<GithubPullrequestEvent> parsePullrequestEvent(JsonObject jsonObject) {

    if (PULLREQUEST_ACTION_CREATED.equals(jsonObject.getString("action"))) {
      Type type = Type.PULLREQUEST_CREATED;
      Pullrequest pullrequest = parsePullrequestPayload(jsonObject.getJsonObject("pull_request"));
      return Optional.of(new GithubPullrequestEvent(type, pullrequest));
    }

    return Optional.empty();
  }

  private Pullrequest parsePullrequestPayload(JsonObject pullrequestPayload) {
    Pullrequest pullRequest = new Pullrequest();
    pullRequest.id = pullrequestPayload.getInt("id");
    pullRequest.url = pullrequestPayload.getString("html_url");
    pullRequest.createdAt = ZonedDateTime.parse(pullrequestPayload.getString("created_at"));
    pullRequest.state = State.OPEN;
    pullRequest.repositoryId = pullrequestPayload.getJsonObject("repo").getInt("id");
    return pullRequest;
  }

  private void handleEvent(JsonObject event) {
    JsonObject eventPayload = (JsonObject) event.get("payload");

    // System.err.println("\r\n\r\n\r\n***\r\n\r\n");
    System.err.println("******** ANY EVENT: ");
    System.err.println(event.getString("created_at"));
    // System.err.println(eventPayload);
    System.err.println("\r\n\r\n\r\n***\r\n\r\n");

    if (isCreatedEvent(event)) {
      if (isRepoCreatedEvent(eventPayload)) {
        System.err.println("\r\n\r\n\r\n***\r\n\r\n");
        System.err.println("################ ******** NEW REPO: ");
        // System.err.println(eventPayload);
        // System.err.println("\r\n\r\n\r\n***\r\n\r\n");
      }
    }
  }

  private boolean isPullRequestCreatedEvent(JsonObject event) {
    return EVENT_TYPE_PULL_REQUEST.equals(event.getString("type"));
  }

  private boolean isCreatedEvent(JsonObject event) {
    return EVENT_TYPE_CREATE.equals(event.getString("type"));
  }

  private boolean isRepoCreatedEvent(JsonObject eventPayload) {
    System.err.println(eventPayload.getString("ref_type"));
    return EVENT_SUBTYPE_REPO.equals(eventPayload.getString("ref_type"));
  }

  private <T> List<T> loadAllPages(String path, Function<JsonObject, T> mapper) throws IOException {
    final JsonResponse resp = client.entry().uri().path(path).back().fetch().as(JsonResponse.class);
    return handleResponse(resp, mapper, path, 1);
  }

  private <T> List<T> handleResponse(JsonResponse resp, Function<JsonObject, T> mapper, String path, int page)
      throws IOException {
    List<T> result = resp
      .json()
      .readArray()
      .stream()
      .filter(v -> v.getValueType() == ValueType.OBJECT)
      .map(v -> (JsonObject) v)
      .map(mapper)
      .collect(Collectors.toList());

    if (resp.headers().keySet().contains("Link")
        && resp.headers().get("Link").stream().anyMatch(s -> s.contains("next"))) {
      resp = client.entry().uri().path(path).queryParam("page", page).back().fetch().as(JsonResponse.class);
      result.addAll(handleResponse(resp, mapper, path, page + 1));
    }

    return result;
  }
}

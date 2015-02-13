package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.Pullrequest.State;
import com.devbliss.gpullr.domain.PullrequestEvent;
import com.devbliss.gpullr.domain.PullrequestEvent.Type;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.devbliss.gpullr.util.Log;
import com.jcabi.github.Github;
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
  private static final String PULLREQUEST_ACTION_CREATED = "opened";
  private static final String HEADER_POLL_INTERVAL = "X-Poll-Interval";
  private static final String HEADER_ETAG = "ETag";

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
          jo -> new Repo(jo.getInt("id"), jo.getString("name"), jo.getString("description")));
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public GithubEventsResponse fetchAllEvents(Repo repo, Optional<String> etagHeader) {
    logger.info("fetch all events for repo: " + repo);
    
    try {
      List<PullrequestEvent> pullrequestEvents = loadAllPages("repos/devbliss/" + repo.name + "/events",
          jo -> parseEvent(jo, repo))
        .stream()
        .filter(optEv -> optEv.isPresent())
        .map(optEv -> optEv.get())
        .collect(Collectors.toList());
      return new GithubEventsResponse(pullrequestEvents, 60, "bla");
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public List<User> fetchAllOrgaMembers() throws IOException {
    return loadAllPages("/orgs/devbliss/members", this::parseUser);
  }
  
  private User parseUser(JsonObject userJson) {
    return new User(userJson.getInt("id"), userJson.getString("login"), userJson.getString("avatar_url"));
  }

  private Optional<PullrequestEvent> parseEvent(JsonObject eventJson, Repo repo) {
    if (isPullRequestCreatedEvent(eventJson)) {
      return parsePullrequestEvent(eventJson, repo);
    }
    return Optional.empty();
  }

  private Optional<PullrequestEvent> parsePullrequestEvent(JsonObject eventJson, Repo repo) {
    Type type = Type.PULLREQUEST_CREATED;
    Pullrequest pullrequest = parsePullrequestPayload(eventJson.getJsonObject("payload").getJsonObject("pull_request"));
    pullrequest.repo = repo;
    return Optional.of(new PullrequestEvent(type, pullrequest));
  }

  private Pullrequest parsePullrequestPayload(JsonObject pullrequestJson) {
    Pullrequest pullRequest = new Pullrequest();
    pullRequest.id = pullrequestJson.getInt("id");
    pullRequest.url = pullrequestJson.getString("html_url");
    pullRequest.createdAt = ZonedDateTime.parse(pullrequestJson.getString("created_at"));
    pullRequest.state = State.OPEN;
    pullRequest.owner = parseUser(pullrequestJson.getJsonObject("user"));
    pullRequest.additions = pullrequestJson.getInt("additions");
    pullRequest.deletions = pullrequestJson.getInt("deletions");
    pullRequest.changedFiles = pullrequestJson.getInt("changed_files");
    return pullRequest;
  }

  private boolean isPullRequestCreatedEvent(JsonObject event) {
    return EVENT_TYPE_PULL_REQUEST.equals(event.getString("type")) &&
        PULLREQUEST_ACTION_CREATED.equals(event.getJsonObject("payload").getString("action"));
  }

  private <T> List<T> loadAllPages(String path, Function<JsonObject, T> mapper) throws IOException {
    final JsonResponse resp = client.entry().uri().path(path).back().fetch().as(JsonResponse.class);

    // if (path.contains("events")) {
    // System.err.println("########## EVENTS RESPONSE: ");
    // for (Entry<String, List<String>> entry : resp.headers().entrySet()) {
    // System.err.println(entry.getKey() + ":: " + entry.getValue());
    // }
    // }

    return handleResponse(resp, mapper, path, 1);
  }

  private <T> List<T> handleResponse(JsonResponse resp, Function<JsonObject, T> mapper, String path, int page)
      throws IOException {
    try {
      JsonReaderFactory jrf = Json.createReaderFactory(null);

      List<T> result =
          // resp
          // .json()//
          jrf.createReader(new ByteArrayInputStream(resp.binary()))
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
    } catch (IllegalStateException e) {
      System.err.println();
      System.err.println("****** ILLEGAL STATE: " + e.getMessage());
      System.err.println(resp);
      System.err.println();
      return new ArrayList<>();
    }
  }
}

package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.GithubRepo;
import com.devbliss.gpullr.exception.UnexpectedException;
import com.jcabi.github.Github;
import com.jcabi.http.response.JsonResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business layer for all operations against the GitHub API, facading the library used for the API calls.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class GithubService {

  private static final String EVENT_TYPE_CREATE = "CreateEvent";
  private static final String EVENT_SUBTYPE_REPO = "repository";

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
  public void loadEvents() throws IOException {
    System.err.println("*** :: events :: ***");

    // WORKS:
    /*
     * loadAllPages("repos/devbliss/ecosystem-grunt-plugin/events", jo -> jo.getString("id") + ": "
     * + jo.getString("type") + ": " + jo.getString("created_at")).forEach( s ->
     * System.out.println(s));
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
  }

  private void handleEvent(JsonObject event) {
    JsonObject eventPayload = (JsonObject) event.get("payload");
    
//    System.err.println("\r\n\r\n\r\n***\r\n\r\n");
    System.err.println("******** ANY EVENT: ");
    System.err.println(event.getString("created_at"));
    //System.err.println(eventPayload);
    System.err.println("\r\n\r\n\r\n***\r\n\r\n");

    if (isCreatedEvent(event)) {
      if (isRepoCreatedEvent(eventPayload)) {
        System.err.println("\r\n\r\n\r\n***\r\n\r\n");
        System.err.println("################ ******** NEW REPO: ");
        //System.err.println(eventPayload);
//        System.err.println("\r\n\r\n\r\n***\r\n\r\n");
      }
    }
  }

  private boolean isCreatedEvent(JsonObject event) {
    System.err.println(event.getString("type"));
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

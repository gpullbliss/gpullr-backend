package com.devbliss.gpullr.service.github;

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

  @Autowired
  private Github client;

  // private GithubFactory githubFactory;

  /**
   * Reads all repositories owned bei devbliss and returns a list of strings in the format name:url
   * @return
   * @throws IOException
   */
  public List<String> proofThatItWorks() throws IOException {
    return loadAllPages("/orgs/devbliss/repos", jo -> jo.getString("name") + " _ " + jo.getString("html_url"));
  }

  public void loadEvents() throws IOException {
    // /repos/:owner/:repo/events
  }

  private <T> List<T> loadAllPages(String path, Function<JsonObject, T> mapper) throws IOException {
    final JsonResponse resp = client.entry().uri().path(path).back().fetch().as(JsonResponse.class);
    return handleResponse(resp, mapper, path, 1);
  }

  private <T> List<T> handleResponse(JsonResponse resp, Function<JsonObject, T> mapper, String path, int page)
      throws IOException {
    System.err.println("handle response page: " + page);
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

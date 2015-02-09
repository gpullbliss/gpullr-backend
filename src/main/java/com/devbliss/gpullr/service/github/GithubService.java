package com.devbliss.gpullr.service.github;

import com.jcabi.github.Github;
import com.jcabi.http.response.JsonResponse;
import java.io.IOException;
import java.util.List;
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
  private GithubFactory githubFactory;

  /**
   * Reads all repositories owned bei devbliss and returns a list of strings in the format name:url
   * @return
   * @throws IOException
   */
  public List<String> proofThatItWorks() throws IOException {
    Github client = githubFactory.createClient();

    final JsonResponse resp = client.entry().uri().path("/orgs/devbliss/repos").back().fetch().as(JsonResponse.class);
    return resp
      .json()
      .readArray()
      .stream()
      .filter(v -> v.getValueType() == ValueType.OBJECT)
      .map(v -> (JsonObject) v)
      .map(o -> o.getString("name") + ": " + o.getString("html_url"))
      .collect(Collectors.toList());
  }
}

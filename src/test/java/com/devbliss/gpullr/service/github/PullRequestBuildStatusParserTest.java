package com.devbliss.gpullr.service.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.BuildStatus;
import com.devbliss.gpullr.util.http.GithubHttpResponse;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests parser by calling it with json payload from {@value #PAYLOAD_FILE_PATH}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestBuildStatusParserTest {

  private static final String PAYLOAD_FILE_PATH = "src/test/resources/buildStatus.json";

  private static final String ETAG = "asefeilw25,sdm";

  private PullRequestBuildStatusParser parser;

  private List<JsonObject> jsonObjects;

  @Mock
  private GithubHttpResponse resp;

  @Before
  public void setup() throws Exception {
    when(resp.getEtag()).thenReturn(Optional.of(ETAG));

    try (BufferedReader br = Files.newBufferedReader(Paths.get(PAYLOAD_FILE_PATH))) {
      jsonObjects = Json
        .createReader(br)
        .readArray()
        .stream()
        .filter(jv -> jv.getValueType() == ValueType.OBJECT)
        .map(jv -> (JsonObject) jv)
        .collect(Collectors.toList());
      parser = new PullRequestBuildStatusParser();
    }
  }

  @Test
  public void parseWithPayload() {
    when(resp.getStatusCode()).thenReturn(200);
    when(resp.getJsonObjects()).thenReturn(Optional.of(jsonObjects));
    GithubPullRequestBuildStatusResponse buildStatusResponse = parser.parse(resp, "Some PullRequest Title");
    assertEquals(3, buildStatusResponse.payload.size());
    assertEquals(BuildStatus.State.SUCCESS, buildStatusResponse.payload.get(0).state);
    assertEquals(BuildStatus.State.PENDING, buildStatusResponse.payload.get(1).state);
    assertEquals(BuildStatus.State.PENDING, buildStatusResponse.payload.get(2).state);
    assertEquals(
        ZonedDateTime.of(LocalDateTime.of(2015, 3, 30, 15, 36, 17), ZoneId.of("Z")),
        buildStatusResponse.payload.get(0).timestamp);
    assertEquals(
        ZonedDateTime.of(LocalDateTime.of(2015, 3, 30, 15, 35, 41), ZoneId.of("Z")),
        buildStatusResponse.payload.get(1).timestamp);
    assertEquals(
        ZonedDateTime.of(LocalDateTime.of(2015, 3, 30, 15, 35, 34), ZoneId.of("Z")),
        buildStatusResponse.payload.get(2).timestamp);
    assertEquals(ETAG, buildStatusResponse.etagHeader.get());
  }

  @Test
  public void parseWithoutPayloadBecauseNotModified() {
    when(resp.getStatusCode()).thenReturn(304);
    when(resp.getJsonObjects()).thenReturn(Optional.empty());
    GithubPullRequestBuildStatusResponse buildStatusResponse = parser.parse(resp, "Some PullRequest Title");
    assertTrue(buildStatusResponse.payload.isEmpty());
    assertEquals(ETAG, buildStatusResponse.etagHeader.get());
  }

  @Test
  public void parseWithoutPayloadBecauseError() {
    when(resp.getStatusCode()).thenReturn(401);
    when(resp.getJsonObjects()).thenReturn(Optional.empty());
    GithubPullRequestBuildStatusResponse buildStatusResponse = parser.parse(resp, "Some PullRequest Title");
    assertTrue(buildStatusResponse.payload.isEmpty());
    assertEquals(ETAG, buildStatusResponse.etagHeader.get());
  }
}

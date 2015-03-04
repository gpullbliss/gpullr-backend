package com.devbliss.gpullr.util.http;

import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;
import javax.json.JsonValue.ValueType;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a HTTP response received from GitHub API. Contains headers, statuscode and the response body (if any) parsed
 * to a list of {@link JsonObject}s.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubHttpResponse {

  private static final Logger logger = LoggerFactory.getLogger(GithubHttpResponse.class);

  private static final int DEFAULT_POLL_INTERVAL = 60;

  private static final String HEADER_POLL_INTERVAL = "X-Poll-Interval";

  private static final String HEADER_ETAG = "ETag";

  public final Optional<List<JsonObject>> jsonObjects;

  public final Optional<JsonObject> jsonObject;

  public final Map<String, String> headers;

  public final int statusCode;

  /**
   * Creates an instance of {@link GithubHttpResponse} from a HttpResponse. Makes sure the http response is closed
   * after processing.
   * 
   * @param resp
   * @return
   */
  public static GithubHttpResponse create(CloseableHttpResponse resp) {
    return new GithubHttpResponse(resp);
  }

  private GithubHttpResponse(CloseableHttpResponse resp) {
    headers = parseHeaders(resp);
    statusCode = resp.getStatusLine().getStatusCode();

    try {
      Optional<JsonStructure> json = parseJson(resp);

      if (json.isPresent()) {
        jsonObjects = parseJsonArrayIfPresent(json.get());
        jsonObject = parseJsonObjectIfPresent(json.get());
      } else {
        jsonObjects = Optional.empty();
        jsonObject = Optional.empty();
      }
    } finally {
      try {
        resp.close();
      } catch (IOException e) {
        throw new UnexpectedException(e);
      }
    }
  }

  public int getPollInterval() {
    String pollIntervalHeader = headers.get(HEADER_POLL_INTERVAL);

    if (pollIntervalHeader == null) {
      logger.debug("No poll interval header set in response, using default = " + DEFAULT_POLL_INTERVAL);
      return DEFAULT_POLL_INTERVAL;
    }

    return Integer.parseInt(pollIntervalHeader);
  }

  public Optional<String> getEtag() {
    return Optional.of(headers.get(HEADER_ETAG));
  }

  private Optional<List<JsonObject>> parseJsonArrayIfPresent(JsonStructure json) {
    if (json.getValueType() == ValueType.ARRAY) {
      JsonArray array = (JsonArray) json;
      return Optional.of(array
        .stream()
        .filter(v -> v.getValueType() == ValueType.OBJECT)
        .map(v -> (JsonObject) v)
        .collect(Collectors.toList()));
    } else {
      return Optional.empty();
    }
  }

  private Optional<JsonObject> parseJsonObjectIfPresent(JsonStructure json) {
    if (json.getValueType() == ValueType.OBJECT) {
      JsonObject obj = (JsonObject) json;
      return Optional.of(obj);
    } else {
      return Optional.empty();
    }
  }

  private Optional<JsonStructure> parseJson(CloseableHttpResponse resp) {
    JsonReaderFactory jrf = Json.createReaderFactory(null);

    if (resp.getEntity() != null) {
      try {
        return Optional.of(jrf.createReader(resp.getEntity().getContent()).read());
      } catch (IOException e) {
        throw new UnexpectedException(e);
      }
    } else {
      return Optional.empty();
    }
  }

  private Map<String, String> parseHeaders(CloseableHttpResponse resp) {
    Map<String, String> headers = new HashMap<>();
    Stream.of(resp.getAllHeaders()).forEach(h -> putHeader(h, headers));
    return headers;
  }

  private void putHeader(Header header, Map<String, String> headers) {
    String val = header.getValue();

    if (headers.containsKey(header.getName())) {
      val = headers.get(header.getName()) + header.getValue();
    }

    headers.put(header.getName(), val);
  }
}

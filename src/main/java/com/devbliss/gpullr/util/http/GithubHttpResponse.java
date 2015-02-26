package com.devbliss.gpullr.util.http;

import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue.ValueType;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Wraps a HTTP response received from GitHub API. Contains headers, statuscode and the response body (if any) parsed
 * to a list of {@link JsonObject}s.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class GithubHttpResponse {

  public final List<JsonObject> jsonObjects;

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
      jsonObjects = parsePayload(resp);
    } catch (IOException e) {
      throw new UnexpectedException(e);
    } finally {
      try {
        resp.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private List<JsonObject> parsePayload(CloseableHttpResponse resp) throws IOException {
    if (resp.getEntity() != null) {
      JsonReaderFactory jrf = Json.createReaderFactory(null);
      return jrf.createReader(resp.getEntity().getContent())
        .readArray()
        .stream()
        .filter(v -> v.getValueType() == ValueType.OBJECT)
        .map(v -> (JsonObject) v)
        .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
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

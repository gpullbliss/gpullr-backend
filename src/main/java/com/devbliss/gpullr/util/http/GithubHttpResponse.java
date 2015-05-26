package com.devbliss.gpullr.util.http;

import com.devbliss.gpullr.domain.ApiRateLimitReachedEvent;
import com.devbliss.gpullr.exception.UnexpectedException;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
import org.springframework.context.ApplicationContext;

/**
 * Wraps a HTTP response received from GitHub API. Contains headers, statuscode and the response body (if any) parsed
 * to a list of {@link JsonObject}s.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public class GithubHttpResponse {

  private static final Logger logger = LoggerFactory.getLogger(GithubHttpResponse.class);

  private static final int RANDOM_ADDITIONAL_SECONDS_RANGE = 120;

  private static final int DEFAULT_POLL_INTERVAL = 60;

  private static final int DEFAULT_WAIT_MINUTES_IF_RATE_LIMIT_EXCEEDED = 60;

  private static final String HEADER_POLL_INTERVAL = "X-Poll-Interval";

  private static final String HEADER_ETAG = "ETag";

  private static final String REMAINING_RATE_LIMIT_HEADER_KEY = "X-RateLimit-Remaining";

  private static final String REMAINING_RATE_RESET_HEADER_KEY = "X-RateLimit-Reset";

  private static final String MSG_NO_HEADER_FOUND = "No %s header found in response.";

  private final Optional<List<JsonObject>> jsonObjects;

  private final Optional<JsonObject> jsonObject;

  public final Map<String, String> headers;

  private final int statusCode;

  public final int rateLimitRemaining;

  public final Optional<ZonedDateTime> rateLimitResetTime;

  public final String uri;

  private ApplicationContext applicationContext;

  /**
   * Creates an instance of {@link GithubHttpResponse} from a HttpResponse. Makes sure the http response is closed
   * after processing.
   *
   * @param resp CloseableHttpResponse from a http client
   * @param uri called uri
   * @param applicationContext ... for application wide event dispatching
   * @return new instance of {@link GithubHttpResponse}
   */
  public static GithubHttpResponse create(CloseableHttpResponse resp, URI uri, ApplicationContext applicationContext) {
    return new GithubHttpResponse(resp, uri, applicationContext);
  }

  private GithubHttpResponse(CloseableHttpResponse resp, URI uri, ApplicationContext applicationContext) {
    headers = parseHeaders(resp);
    this.uri = uri.toString();
    this.applicationContext = applicationContext;
    statusCode = resp.getStatusLine().getStatusCode();
    rateLimitRemaining = parseRemainingRateLimit(headers);
    rateLimitResetTime = parseRateLimitResetTime(headers);

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

  /**
   * Tells when the next request for the same resource is allowed, respecting the restrictions from GitHub:
   * <p>
   * Normally, this is simply now plus number of seconds stated in poll interval header defaulting to 60 seconds.
   * However, when the rate limit has been exceeded, this is the value of the rate limit reset time header (defaulting
   * to one hour from now) plus a random number of seconds.
   *
   * @return next point in time to fetch next data
   */
  public Instant getNextFetch() {

    if (rateLimitRemaining < 1) {
      Random random = new Random();
      int addition = random.nextInt(RANDOM_ADDITIONAL_SECONDS_RANGE);

      Instant nextFetchOnRateLimit;

      if (rateLimitResetTime.isPresent()) {
        logger.debug("Ratelimit exceeded, next fetch at reset time plus random: " + addition);
        nextFetchOnRateLimit = Instant
            .from(rateLimitResetTime.get())
            .plusSeconds(addition);
      } else {
        logger.debug("Ratelimit exceeded, next fetch at default reset time plus random: " + addition);
        nextFetchOnRateLimit = Instant
            .now()
            .plus(Duration.of(DEFAULT_WAIT_MINUTES_IF_RATE_LIMIT_EXCEEDED, ChronoUnit.MINUTES))
            .plusSeconds(addition);
      }

      applicationContext.publishEvent(new ApiRateLimitReachedEvent(this, nextFetchOnRateLimit));

      return nextFetchOnRateLimit;
    }

    String pollIntervalHeader = headers.get(HEADER_POLL_INTERVAL);
    int secondsFromNow;

    if (pollIntervalHeader == null) {
      secondsFromNow = DEFAULT_POLL_INTERVAL;
    } else {
      secondsFromNow = Integer.parseInt(pollIntervalHeader);
    }

    return Instant.now().plusSeconds(secondsFromNow);
  }

  public Optional<String> getEtag() {
    return Optional.ofNullable(headers.get(HEADER_ETAG));
  }

  public String getFormattedRateLimitResetTime() {
    if (rateLimitResetTime.isPresent()) {
      return rateLimitResetTime.get().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } else {
      return "??";
    }
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Optional<List<JsonObject>> getJsonObjects() {
    return jsonObjects;
  }

  public Optional<JsonObject> getJsonObject() {
    return jsonObject;
  }

  private int parseRemainingRateLimit(Map<String, String> headers) {
    String header = headers.get(REMAINING_RATE_LIMIT_HEADER_KEY);

    if (header != null) {
      return Integer.parseInt(header);
    } else {
      logger.warn(String.format(MSG_NO_HEADER_FOUND, REMAINING_RATE_LIMIT_HEADER_KEY));
      return 0;
    }
  }

  private Optional<ZonedDateTime> parseRateLimitResetTime(Map<String, String> headers) {
    String header = headers.get(REMAINING_RATE_RESET_HEADER_KEY);

    if (header != null) {
      long epoch = Long.valueOf(header);
      return Optional.of(ZonedDateTime
          .ofInstant(Instant.ofEpochSecond(epoch), ZoneId.of("UTC"))
          .withZoneSameInstant(ZoneId.systemDefault()));
    } else {
      logger.warn(String.format(MSG_NO_HEADER_FOUND, REMAINING_RATE_RESET_HEADER_KEY));
      return Optional.empty();
    }
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

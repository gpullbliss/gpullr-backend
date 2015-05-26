package com.devbliss.gpullr.util.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.ApiRateLimitReachedEvent;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

/**
 * Unit tests for {@link GithubHttpResponse}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubHttpResponseUnitTest {

  private static final String URI_STRING = "http://example.com";

  private static final int STATUS_CODE = 200;

  private static final int RATE_LIMIT_REMAINING = 4212;

  @Mock
  private CloseableHttpResponse resp;

  @Mock
  private ApplicationContext applicationContext;

  @Mock
  private StatusLine statusLine;

  @Mock
  private ApiRateLimitReachedEvent apiRateLimitReachedEvent;

  private URI uri;

  @Before
  public void setup() throws Exception {
    uri = new URI(URI_STRING);
    when(statusLine.getStatusCode()).thenReturn(STATUS_CODE);
    when(resp.getStatusLine()).thenReturn(statusLine);
  }

  @Test
  public void correctValueInitialization() {
    final String rateLimitReset = "1426238742"; // = Fri Mar 13 10:25:42 2015 GMT+1:00

    fakeHeaders(
        fakeHeader("X-RateLimit-Remaining", Integer.toString(RATE_LIMIT_REMAINING)),
        fakeHeader("X-RateLimit-Reset", rateLimitReset));
    GithubHttpResponse githubHttpResponse = GithubHttpResponse.create(resp, uri, applicationContext);
    assertEquals(RATE_LIMIT_REMAINING, githubHttpResponse.rateLimitRemaining);
    assertTrue(githubHttpResponse.rateLimitResetTime.isPresent());
    assertEquals(STATUS_CODE, githubHttpResponse.getStatusCode());
    assertEquals(URI_STRING, githubHttpResponse.uri);

    verify(applicationContext, never()).publishEvent(any(ApiRateLimitReachedEvent.class));
  }

  @Test
  public void nextFetchWhenRateLimitOkAndPollIntervalHeaderSet() {
    final int nextPollInSeconds = 90;
    fakeHeaders(
        fakeHeader("X-RateLimit-Remaining", Integer.toString(RATE_LIMIT_REMAINING)),
        fakeHeader("X-Poll-Interval", Integer.toString(nextPollInSeconds)));
    GithubHttpResponse githubHttpResponse = GithubHttpResponse.create(resp, uri, applicationContext);
    assertInstantsAboutTheSame(Instant.now().plusSeconds(nextPollInSeconds), githubHttpResponse.getNextFetch());

    verify(applicationContext, never()).publishEvent(any(ApiRateLimitReachedEvent.class));
  }

  @Test
  public void nextFetchWhenRateLimitOkAndPollIntervalHeaderNotSet() {
    final int defaultPollInSeconds = 60;
    fakeHeaders(fakeHeader("X-RateLimit-Remaining", Integer.toString(RATE_LIMIT_REMAINING)));
    GithubHttpResponse githubHttpResponse = GithubHttpResponse.create(resp, uri, applicationContext);
    assertInstantsAboutTheSame(Instant.now().plusSeconds(defaultPollInSeconds), githubHttpResponse.getNextFetch());

    verify(applicationContext, never()).publishEvent(any(ApiRateLimitReachedEvent.class));
  }

  @Test
  public void nextFetchWhenRateLimitExceededAndResetHeaderSet() {
    Instant reset = Instant.now().plus(Duration.of(17, ChronoUnit.MINUTES));
    fakeHeaders(
        fakeHeader("X-RateLimit-Remaining", "0"),
        fakeHeader("X-RateLimit-Reset", Long.toString(reset.getEpochSecond())));
    GithubHttpResponse githubHttpResponse = GithubHttpResponse.create(resp, uri, applicationContext);
    Instant nextFetch = githubHttpResponse.getNextFetch();

    // next fetch supposed to start at a random time within 120 seconds after reset time set in
    // response header:
    assertTrue(reset.plusSeconds(121).isAfter(nextFetch));
    assertTrue(reset.minusSeconds(1).isBefore(nextFetch));

    verify(applicationContext).publishEvent(any(ApiRateLimitReachedEvent.class));
  }

  @Test
  public void nextFetchWhenRateLimitExceededAndResetHeaderNotSet() {
    fakeHeaders(fakeHeader("X-RateLimit-Remaining", "0"));
    GithubHttpResponse githubHttpResponse = GithubHttpResponse.create(resp, uri, applicationContext);
    Instant nextFetch = githubHttpResponse.getNextFetch();

    // next fetch supposed to start at a random time within 120 seconds after default reset time (60
    // minutes):
    Instant oneHourLater = Instant.now().plus(Duration.of(60, ChronoUnit.MINUTES));
    assertTrue(oneHourLater.plusSeconds(121).isAfter(nextFetch));
    assertTrue(oneHourLater.minusSeconds(1).isBefore(nextFetch));

    verify(applicationContext).publishEvent(any(ApiRateLimitReachedEvent.class));
  }

  private void assertInstantsAboutTheSame(Instant i0, Instant i1) {
    assertTrue("Instants " + i0 + " and " + i1 + " expected to be the about the same but aren't.",
        Math.abs(i0.getEpochSecond() - i1.getEpochSecond()) <= 1L);
  }

  private Header fakeHeader(String key, String value) {
    Header header = mock(Header.class);
    when(header.getName()).thenReturn(key);
    when(header.getValue()).thenReturn(value);
    return header;
  }

  private void fakeHeaders(Header... headers) {
    when(resp.getAllHeaders()).thenReturn(headers);
  }
}

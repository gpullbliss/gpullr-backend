package com.devbliss.gpullr.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.service.github.GithubEventsResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Produces {@link GithubApi} an instance for test purposes that is actually a Mockito mock in order to allow
 * verify calls on it.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class GithubApiMockFactory {

  private GithubEventsResponse githubEventsResponse;

  @PostConstruct
  public void initMocks() {
    githubEventsResponse = new GithubEventsResponse(new ArrayList<>(), Instant.now().plusSeconds(60), Optional.empty());
  }

  @SuppressWarnings("unchecked")
  @Bean
  @Profile("test")
  @Primary
  public GithubApi createGithubTestApi() {
    GithubApi githubApi = mock(GithubApi.class);
    when(githubApi.fetchAllEvents(any(Repo.class), any(Optional.class))).thenReturn(githubEventsResponse);
    return githubApi;
  }
}

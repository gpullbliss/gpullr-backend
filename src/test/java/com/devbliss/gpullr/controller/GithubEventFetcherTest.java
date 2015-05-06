package com.devbliss.gpullr.controller;

import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.RepoCreatedEvent;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class GithubEventFetcherTest {

  private static final String ORIGINAL_REPO_NAME = "sometestrepo";

  @Autowired
  private GithubEventFetcher githubEventFetcher;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private RepoService repoService;

  @Autowired
  private TaskScheduler taskScheduler;

  private Repo repo;

  @Before
  public void setup() {
    repo = new Repo(1, ORIGINAL_REPO_NAME, "");
    repoService.setActiveRepos(Arrays.asList(repo));
  }

  @Test
  public void githubEventFetcherListensToRepoCreatedEvent() {
    applicationContext.publishEvent(new RepoCreatedEvent(this, repo));
    verify(githubApi).fetchAllEvents(repo, Optional.empty());
  }

  @Test
  public void fetchLoop() {
    githubEventFetcher.startFetchEventsLoop();
    // verify(taskScheduler).schedule(task, startTime) ==> continue
  }
}

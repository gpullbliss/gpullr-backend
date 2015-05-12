package com.devbliss.gpullr.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.RepoCreatedEvent;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

  private static final int REPO_ID = 1;

  private static final String ORIGINAL_REPO_NAME = "sometestrepo";

  private static final String RENAMED_REPO_NAME = "sometestrepo_renamed";

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

  private ArgumentCaptor<Repo> repoCaptor;

  private ArgumentCaptor<Runnable> fetchRunnable;

  private Repo repo;

  @Before
  public void setup() {
    repo = new Repo(REPO_ID, ORIGINAL_REPO_NAME, "");
    repoService.setActiveRepos(Arrays.asList(repo));
    fetchRunnable = ArgumentCaptor.forClass(Runnable.class);
    repoCaptor = ArgumentCaptor.forClass(Repo.class);
  }

  @Test
  public void githubEventFetcherListensToRepoCreatedEvent() {
    /*
     * GithubEventFetcher#startFetchEventsLoop is automatically called at startup. The task
     * scheduler is called 3 times, 2 of them being the initial launch at startup, and the 3rd time
     * being the one triggered by the publish event:
     */
    verify(githubApi, times(2)).fetchAllEvents(repo, Optional.empty());
    applicationContext.publishEvent(new RepoCreatedEvent(this, repo));
    verify(githubApi, times(3)).fetchAllEvents(repo, Optional.empty());
  }

  @Test
  public void fetchLoop() {
    /*
     * GithubEventFetcher#startFetchEventsLoop is automatically called at startup. The task
     * scheduler is called 3 times, 2 of them being the initial launch at startup, and the 3rd time
     * being the one triggered by the repsonse handler method:
     */
    verify(taskScheduler, times(3)).schedule(fetchRunnable.capture(), any(Date.class));

    /*
     * The second captured runnable is the one of the initial fetch call at startup:
     */
    Runnable initialFetchRunnable = fetchRunnable.getAllValues().get(1);

    /*
     * The third captured runnable is the one of the refresh call scheduled when handling response:
     */
    Runnable fetchAgainRunnable = fetchRunnable.getAllValues().get(2);

    // the inital fetch is done with the original values:
    initialFetchRunnable.run();
    verify(githubApi).fetchAllEvents(repoCaptor.capture(), eq(Optional.empty()));
    assertEquals(ORIGINAL_REPO_NAME, repoCaptor.getValue().name);

    // rename repo (use new instance to simulate different thread):
    Repo refreshedRepo = new Repo(REPO_ID, RENAMED_REPO_NAME, "");
    repoService.setActiveRepos(Arrays.asList(refreshedRepo));

    // the scheduled follow-up fetch should happen with the new name:
    fetchAgainRunnable.run();
    verify(githubApi, times(2)).fetchAllEvents(repoCaptor.capture(), eq(Optional.empty()));
    assertEquals(RENAMED_REPO_NAME, repoCaptor.getAllValues().get(2).name);
  }
}

package com.devbliss.gpullr.service.github;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.scheduling.TaskScheduler;

/**
 * Unit test with mocks for {@link PullRequestWatcher}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestWatcherUnitTest {

  private static final int PR_ID = 15;

  @Mock
  private TaskScheduler taskScheduler;

  @Mock
  private Repo repo;

  @Mock
  private Logger logger;

  @Mock
  private PullRequestWatchThreadProducer pullRequestWatchThreadProducer;

  @Mock
  private PullRequestWatchThread pullRequestWatchThread;

  private PullRequest pullRequest;

  private PullRequestWatcher pullRequestWatcher;

  @Before
  public void setup() {
    pullRequest = new PullRequest();
    pullRequest.id = PR_ID;
    pullRequest.repo = repo;
    when(pullRequestWatchThreadProducer.createThread(pullRequest)).thenReturn(pullRequestWatchThread);
    pullRequestWatcher = new PullRequestWatcher(taskScheduler, pullRequestWatchThreadProducer);
    pullRequestWatcher.logger = logger;
  }

  @Test
  public void startWatching() {
    pullRequestWatcher.startWatching(pullRequest);
    verify(taskScheduler).schedule(eq(pullRequestWatchThread), any(Date.class));

    // calling the method with the same pull request once more should have no effect:
    pullRequestWatcher.startWatching(pullRequest);
    verify(taskScheduler).schedule(eq(pullRequestWatchThread), any(Date.class));
  }

  @Test
  public void stopWatching() {
    // first we have to start something before we can stop it:
    pullRequestWatcher.startWatching(pullRequest);
    verify(taskScheduler).schedule(eq(pullRequestWatchThread), any(Date.class));

    pullRequestWatcher.stopWatching(pullRequest);
    verify(pullRequestWatchThread).pleaseStop();
  }
}

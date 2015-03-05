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
 * Unit test with mocks for {@link PullRequestAssigneeWatcher}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestAssigneeWatcherUnitTest {

  private static final int PR_ID = 15;

  @Mock
  private TaskScheduler taskScheduler;

  @Mock
  private Repo repo;

  @Mock
  private Logger logger;

  @Mock
  private PullRequestAssigneeWatchThreadProducer pullRequestAssigneeWatchThreadProducer;

  @Mock
  private PullRequestAssigneeWatchThread pullRequestAssigneeWatchThread;

  private PullRequest pullRequest;

  private PullRequestAssigneeWatcher pullRequestAssigneeWatcher;

  @Before
  public void setup() {
    pullRequest = new PullRequest();
    pullRequest.id = PR_ID;
    pullRequest.repo = repo;
    when(pullRequestAssigneeWatchThreadProducer.createThread(pullRequest)).thenReturn(pullRequestAssigneeWatchThread);
    pullRequestAssigneeWatcher = new PullRequestAssigneeWatcher(taskScheduler, pullRequestAssigneeWatchThreadProducer);
    pullRequestAssigneeWatcher.logger = logger;
  }

  @Test
  public void startWatching() {
    pullRequestAssigneeWatcher.startWatching(pullRequest);
    verify(taskScheduler).schedule(eq(pullRequestAssigneeWatchThread), any(Date.class));

    // calling the method with the same pull request once more should have no effect:
    pullRequestAssigneeWatcher.startWatching(pullRequest);
    verify(taskScheduler).schedule(eq(pullRequestAssigneeWatchThread), any(Date.class));
  }

  @Test
  public void stopWatching() {
    // first we have to start something before we can stop it:
    pullRequestAssigneeWatcher.startWatching(pullRequest);
    verify(taskScheduler).schedule(eq(pullRequestAssigneeWatchThread), any(Date.class));

    pullRequestAssigneeWatcher.stopWatching(pullRequest);
    verify(pullRequestAssigneeWatchThread).pleaseStop();
  }
}

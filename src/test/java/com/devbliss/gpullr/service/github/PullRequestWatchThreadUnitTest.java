package com.devbliss.gpullr.service.github;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.PullRequestService;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

/**
 * Unit test with mocks for {@link PullRequestWatchThread}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestWatchThreadUnitTest {

  private static final String ETAG = "abc123def";

  private static final int PULLREQUEST_ID = 999;

  @Mock
  private TaskScheduler taskScheduler;

  @Mock
  private GithubApi githubApi;

  @Mock
  private PullRequestService pullRequestService;

  @Mock
  private User assignee;

  @Mock
  private Repo repo;

  @Captor
  private ArgumentCaptor<PullRequest> pullRequestCaptor;

  @Captor
  private ArgumentCaptor<Runnable> runnableCaptor;

  private PullRequest pullRequest;

  private PullRequestWatchThread pullRequestWatchThread;

  private GithubPullRequestResponse githubPullRequestResponse;

  private Optional<String> emptyEtagHeader;

  private Optional<String> nonEmptyEtagHeader;

  @Before
  public void setup() {
    emptyEtagHeader = Optional.empty();
    nonEmptyEtagHeader = Optional.of(ETAG);
    pullRequest = new PullRequest();
    pullRequest.id = PULLREQUEST_ID;
    pullRequest.repo = repo;
    githubPullRequestResponse = new GithubPullRequestResponse(
        Optional.of(pullRequest),
        Instant.now().plusSeconds(60),
        nonEmptyEtagHeader);
    when(githubApi.fetchPullRequest(pullRequest, emptyEtagHeader)).thenReturn(githubPullRequestResponse);
    when(githubApi.fetchPullRequest(pullRequest, nonEmptyEtagHeader)).thenReturn(githubPullRequestResponse);
    when(pullRequestService.findById(PULLREQUEST_ID)).thenReturn(Optional.of(pullRequest));
    pullRequestWatchThread = new PullRequestWatchThread(pullRequest.id, taskScheduler, githubApi,
        pullRequestService);
  }

  @Test
  public void fetchAndHandleResponseWithAssignee() {
    pullRequest.assignee = assignee;
    pullRequestWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(assignee, pullRequestCaptor.getValue().assignee);
  }

  @Test
  public void scheduleNextFetchIfNotStopped() {
    // verify that a task is scheduled after fetching:
    pullRequestWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(taskScheduler).schedule(runnableCaptor.capture(), any(Date.class));

    // make sure it is the right task, i.e. it makes a new request, this time with ETAG header:
    runnableCaptor.getValue().run();
    verify(githubApi).fetchPullRequest(pullRequest, nonEmptyEtagHeader);
  }

  @Test
  public void dontScheduleNextFetchIfStopped() {
    pullRequestWatchThread.pleaseStop();
    pullRequestWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));
  }
}

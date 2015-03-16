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
 * Unit test with mocks for {@link PullRequestAssigneeWatchThread}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestAssigneeWatchThreadUnitTest {

  private static final String ETAG = "abc123def";

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

  private PullRequestAssigneeWatchThread pullRequestAssigneeWatchThread;

  private GithubPullrequestResponse githubPullrequestResponse;

  private Optional<String> emptyEtagHeader;

  private Optional<String> nonEmptyEtagHeader;

  @Before
  public void setup() {
    emptyEtagHeader = Optional.empty();
    nonEmptyEtagHeader = Optional.of(ETAG);
    pullRequest = new PullRequest();
    pullRequest.repo = repo;
    githubPullrequestResponse = new GithubPullrequestResponse(
        Optional.of(pullRequest),
        Instant.now().plusSeconds(60),
        nonEmptyEtagHeader);
    when(githubApi.fetchPullRequest(pullRequest, emptyEtagHeader)).thenReturn(githubPullrequestResponse);
    when(githubApi.fetchPullRequest(pullRequest, nonEmptyEtagHeader)).thenReturn(githubPullrequestResponse);
    pullRequestAssigneeWatchThread = new PullRequestAssigneeWatchThread(pullRequest.id, taskScheduler, githubApi,
        pullRequestService);
  }

  @Test
  public void fetchAndHandleResponseWithAssignee() {
    pullRequest.assignee = assignee;
    pullRequestAssigneeWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(assignee, pullRequestCaptor.getValue().assignee);
  }

  @Test
  public void fetchAndHandleResponseWithoutAssignee() {
    pullRequest.assignee = null;
    pullRequestAssigneeWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(pullRequestService, never()).insertOrUpdate(any(PullRequest.class));
  }

  @Test
  public void scheduleNextFetchIfNotStopped() {
    // verify that a task is scheduled after fetching:
    pullRequestAssigneeWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(taskScheduler).schedule(runnableCaptor.capture(), any(Date.class));

    // make sure it is the right task, i.e. it makes a new request, this time with ETAG header:
    runnableCaptor.getValue().run();
    verify(githubApi).fetchPullRequest(pullRequest, nonEmptyEtagHeader);
  }

  @Test
  public void dontScheduleNextFetchIfStopped() {
    pullRequestAssigneeWatchThread.pleaseStop();
    pullRequestAssigneeWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));
  }
}

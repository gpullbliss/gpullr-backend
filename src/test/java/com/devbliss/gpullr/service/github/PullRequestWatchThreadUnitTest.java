package com.devbliss.gpullr.service.github;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.BuildStatus;
import com.devbliss.gpullr.domain.BuildStatus.State;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.util.http.GithubHttpResponse;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

  private static final ZonedDateTime NOW = ZonedDateTime.now();

  private static final ZonedDateTime TEN_MINUTES_AGO = NOW.minusMinutes(10);

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

  @Mock
  private GithubHttpResponse resp;

  @Captor
  private ArgumentCaptor<PullRequest> pullRequestCaptor;

  @Captor
  private ArgumentCaptor<Runnable> runnableCaptor;

  private PullRequest pullRequest;

  private PullRequestWatchThread pullRequestWatchThread;

  private GithubPullRequestResponse githubPullRequestResponse;

  private GithubPullRequestBuildStatusResponse githubPullRequestBuildStatusResponse;

  private Optional<String> emptyEtagHeader;

  private Optional<String> nonEmptyEtagHeader;

  private List<BuildStatus> buildStates;

  private BuildStatus latestBuildStatus;

  private BuildStatus earlierBuildStatus;

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
    latestBuildStatus = new BuildStatus(State.PENDING, NOW, null);
    earlierBuildStatus = new BuildStatus(State.FAILURE, TEN_MINUTES_AGO, null);
    buildStates = Arrays.asList(latestBuildStatus, earlierBuildStatus);
    githubPullRequestBuildStatusResponse = new GithubPullRequestBuildStatusResponse(buildStates, resp);
    when(githubApi.fetchPullRequest(pullRequest, emptyEtagHeader)).thenReturn(githubPullRequestResponse);
    when(githubApi.fetchPullRequest(pullRequest, nonEmptyEtagHeader)).thenReturn(githubPullRequestResponse);
    when(githubApi.fetchBuildStatus(pullRequest, emptyEtagHeader)).thenReturn(githubPullRequestBuildStatusResponse);
    when(githubApi.fetchBuildStatus(pullRequest, nonEmptyEtagHeader)).thenReturn(githubPullRequestBuildStatusResponse);
    when(pullRequestService.findById(PULLREQUEST_ID)).thenReturn(Optional.of(pullRequest));
    pullRequestWatchThread = new PullRequestWatchThread(pullRequest.id, taskScheduler, githubApi,
        pullRequestService);
  }

  @Test
  public void fetchAndHandleResponsesWithAssigneeAndBuildStatus() {
    pullRequest.assignee = assignee;
    pullRequestWatchThread.run();

    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(assignee, pullRequestCaptor.getValue().assignee);

    verify(githubApi).fetchBuildStatus(pullRequest, emptyEtagHeader);
    verify(pullRequestService).saveBuildstatus(PULLREQUEST_ID, latestBuildStatus);
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
    verify(githubApi).fetchBuildStatus(pullRequest, nonEmptyEtagHeader);
  }

  @Test
  public void dontScheduleNextFetchIfStopped() {
    pullRequestWatchThread.pleaseStop();
    pullRequestWatchThread.run();
    verify(githubApi).fetchPullRequest(pullRequest, emptyEtagHeader);
    verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));
  }
}

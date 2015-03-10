package com.devbliss.gpullr.service.github;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.PullRequestEvent.Action;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.UserStatisticsService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

/**
 * Unit test for correct pull request event handling. All external dependencies are mocked.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestEventHandlerTest {

  private static final Integer PR_ID = 1981;

  @Mock
  private PullRequestService pullRequestService;

  @Mock
  private PullRequestAssigneeWatcher pullRequestAssigneeWatcher;

  @Mock
  private UserStatisticsService userStatisticsService;

  @Captor
  private ArgumentCaptor<PullRequest> pullRequestCaptor;

  private PullRequest existingPullRequest;

  private PullRequest pullRequestFromResponse;

  private PullRequestEventHandler pullRequestEventHandler;

  @Before
  public void setup() {
    pullRequestEventHandler = new PullRequestEventHandler(
        pullRequestService,
        pullRequestAssigneeWatcher,
        userStatisticsService);
    pullRequestEventHandler.logger = LoggerFactory.getLogger(PullRequestEventHandler.class);
    existingPullRequest = new PullRequest();
    existingPullRequest.id = PR_ID;
    pullRequestFromResponse = new PullRequest();
    pullRequestFromResponse.id = PR_ID;
  }

  @Test
  public void handleOpenedEventWithoutExistingPullRequest() {
    // assume the PR does not exist yet:
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.empty());

    // handle event:
    pullRequestEventHandler.handlePullRequestEvent(new PullRequestEvent(Action.OPENED, pullRequestFromResponse));

    // verify PR is stored with state OPEN and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);

    // verify watcher is started for the pull request:
    verify(pullRequestAssigneeWatcher).startWatching(pullRequestFromResponse);
  }

  @Test
  public void handleOpenedEventWithExistingOpenPullRequest() {
    // assume the PR exists and has state OPEN:
    existingPullRequest.state = State.OPEN;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullRequest));

    // handle event:
    pullRequestEventHandler.handlePullRequestEvent(new PullRequestEvent(Action.OPENED, pullRequestFromResponse));

    // verify PR is stored with state OPEN and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);

    // verify watcher is started for the pull request:
    verify(pullRequestAssigneeWatcher).startWatching(pullRequestFromResponse);
  }

  @Test
  public void handleOpenedEventWithExistingClosedPullRequest() {
    // assume the PR exists and has state CLOSED:
    existingPullRequest.state = State.CLOSED;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullRequest));

    // handle event:
    pullRequestEventHandler.handlePullRequestEvent(new PullRequestEvent(Action.OPENED, pullRequestFromResponse));

    // verify PR is stored with state CLOSED and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.CLOSED, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);

    // verify watcher is stopped for the pull request:
    verify(pullRequestAssigneeWatcher).stopWatching(pullRequestFromResponse);
  }

  @Test
  public void handleReopenedEventWithExistingOpenPullRequest() {
    // assume the PR exists and has state OPEN:
    existingPullRequest.state = State.OPEN;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullRequest));

    // handle event:
    pullRequestEventHandler.handlePullRequestEvent(new PullRequestEvent(Action.REOPENED, pullRequestFromResponse));

    // verify PR is stored with state OPENED and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);

    // verify watcher is started for the pull request:
    verify(pullRequestAssigneeWatcher).startWatching(pullRequestFromResponse);
  }

  @Test
  public void handleReopenedEventWithExistingClosedPullRequest() {
    // assume the PR exists and has state CLOSED:
    existingPullRequest.state = State.CLOSED;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullRequest));

    // handle event:
    pullRequestEventHandler.handlePullRequestEvent(new PullRequestEvent(Action.REOPENED, pullRequestFromResponse));

    // verify PR is stored with state OPENED and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);

    // verify watcher is started for the pull request:
    verify(pullRequestAssigneeWatcher).startWatching(pullRequestFromResponse);
  }
}

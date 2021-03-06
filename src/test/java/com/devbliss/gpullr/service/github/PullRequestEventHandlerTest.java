package com.devbliss.gpullr.service.github;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.PullRequestEvent.Action;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.RankingService;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
  private PullRequestWatcher pullRequestWatcher;

  @Mock
  private RankingService rankingService;

  @Captor
  private ArgumentCaptor<PullRequest> pullRequestCaptor;

  private PullRequest existingPullRequest;

  private PullRequest pullRequestFromResponse;

  private PullRequestEventHandler pullRequestEventHandler;

  private Repo testRepo;

  @Before
  public void setup() {
    pullRequestEventHandler = new PullRequestEventHandler(pullRequestService, pullRequestWatcher);
    pullRequestEventHandler.logger = LoggerFactory.getLogger(PullRequestEventHandler.class);

    testRepo = new Repo();
    testRepo.name = "Test";
    testRepo.id = 1;

    existingPullRequest = new PullRequest();
    existingPullRequest.id = PR_ID;
    existingPullRequest.number = 1;
    existingPullRequest.repo = testRepo;

    pullRequestFromResponse = new PullRequest();
    pullRequestFromResponse.id = PR_ID;
    pullRequestFromResponse.repo = testRepo;
    pullRequestFromResponse.number = 2;

    existingPullRequest.updatedAt = ZonedDateTime.now().minus(2, ChronoUnit.MINUTES);
    pullRequestFromResponse.updatedAt = ZonedDateTime.now();

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
    verify(pullRequestWatcher).startWatching(pullRequestFromResponse);
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
    verify(pullRequestWatcher).startWatching(pullRequestFromResponse);
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
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);

    // verify watcher is stopped for the pull request:
    verify(pullRequestWatcher).startWatching(pullRequestFromResponse);
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
    verify(pullRequestWatcher).startWatching(pullRequestFromResponse);
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
    verify(pullRequestWatcher).startWatching(pullRequestFromResponse);
  }
}

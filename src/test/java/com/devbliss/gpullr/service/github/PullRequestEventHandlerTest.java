package com.devbliss.gpullr.service.github;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.PullRequestEvent.Action;
import com.devbliss.gpullr.service.PullRequestService;
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

  @Captor
  private ArgumentCaptor<PullRequest> pullRequestCaptor;

  private PullRequest existingpullRequest;

  private PullRequestEventHandler pullRequestEventHandler;

  @Before
  public void setup() {
    pullRequestEventHandler = new PullRequestEventHandler(pullRequestService);
    pullRequestEventHandler.logger = LoggerFactory.getLogger(PullRequestEventHandler.class);
    existingpullRequest = new PullRequest();
    existingpullRequest.id = PR_ID;
  }

  @Test
  public void handleOpenedEventWithoutExistingpullRequest() {
    // assume the PR does not exist yet:
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.empty());

    // handle event:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID;
    pullRequestEventHandler.handlepullRequestEvent(new PullRequestEvent(Action.OPENED, pullRequest));

    // verify PR is stored with state OPEN and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);
  }

  @Test
  public void handleOpenedEventWithExistingOpenpullRequest() {
    // assume the PR exists and has state OPEN:
    existingpullRequest.state = State.OPEN;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingpullRequest));
    
    // handle event:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID;
    pullRequestEventHandler.handlepullRequestEvent(new PullRequestEvent(Action.OPENED, pullRequest));

    // verify PR is stored with state OPEN and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);
  }
  
  @Test
  public void handleOpenedEventWithExistingClosedpullRequest() {
    // assume the PR exists and has state CLOSED:
    existingpullRequest.state = State.CLOSED;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingpullRequest));
    
    // handle event:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID;
    pullRequestEventHandler.handlepullRequestEvent(new PullRequestEvent(Action.OPENED, pullRequest));

    // verify PR is stored with state CLOSED and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.CLOSED, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);
  }
  
  @Test
  public void handleReopenedEventWithExistingOpenpullRequest() {
    // assume the PR exists and has state OPEN:
    existingpullRequest.state = State.OPEN;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingpullRequest));
    
    // handle event:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID;
    pullRequestEventHandler.handlepullRequestEvent(new PullRequestEvent(Action.REOPENED, pullRequest));

    // verify PR is stored with state OPENED and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);
  }
  
  @Test
  public void handleReopenedEventWithExistingClosedpullRequest() {
    // assume the PR exists and has state CLOSED:
    existingpullRequest.state = State.CLOSED;
    when(pullRequestService.findById(PR_ID)).thenReturn(Optional.of(existingpullRequest));
    
    // handle event:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID;
    pullRequestEventHandler.handlepullRequestEvent(new PullRequestEvent(Action.REOPENED, pullRequest));

    // verify PR is stored with state OPENED and correct id:
    verify(pullRequestService).findById(PR_ID);
    verify(pullRequestService).insertOrUpdate(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
    assertEquals(PR_ID, pullRequestCaptor.getValue().id);
  }
}

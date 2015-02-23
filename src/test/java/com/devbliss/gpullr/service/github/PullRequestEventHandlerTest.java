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
  private PullRequestService pullrequestService;

  @Captor
  private ArgumentCaptor<PullRequest> pullrequestCaptor;

  private PullRequest existingPullrequest;

  private PullRequestEventHandler pullrequestEventHandler;

  @Before
  public void setup() {
    pullrequestEventHandler = new PullRequestEventHandler(pullrequestService);
    pullrequestEventHandler.logger = LoggerFactory.getLogger(PullRequestEventHandler.class);
    existingPullrequest = new PullRequest();
    existingPullrequest.id = PR_ID;
  }

  @Test
  public void handleOpenedEventWithoutExistingPullrequest() {
    // assume the PR does not exist yet:
    when(pullrequestService.findById(PR_ID)).thenReturn(Optional.empty());

    // handle event:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID;
    pullrequestEventHandler.handlePullrequestEvent(new PullRequestEvent(Action.OPENED, pullrequest));

    // verify PR is stored with state OPEN and correct id:
    verify(pullrequestService).findById(PR_ID);
    verify(pullrequestService).insertOrUpdate(pullrequestCaptor.capture());
    assertEquals(State.OPEN, pullrequestCaptor.getValue().state);
    assertEquals(PR_ID, pullrequestCaptor.getValue().id);
  }

  @Test
  public void handleOpenedEventWithExistingOpenPullrequest() {
    // assume the PR exists and has state OPEN:
    existingPullrequest.state = State.OPEN;
    when(pullrequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullrequest));
    
    // handle event:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID;
    pullrequestEventHandler.handlePullrequestEvent(new PullRequestEvent(Action.OPENED, pullrequest));

    // verify PR is stored with state OPEN and correct id:
    verify(pullrequestService).findById(PR_ID);
    verify(pullrequestService).insertOrUpdate(pullrequestCaptor.capture());
    assertEquals(State.OPEN, pullrequestCaptor.getValue().state);
    assertEquals(PR_ID, pullrequestCaptor.getValue().id);
  }
  
  @Test
  public void handleOpenedEventWithExistingClosedPullrequest() {
    // assume the PR exists and has state CLOSED:
    existingPullrequest.state = State.CLOSED;
    when(pullrequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullrequest));
    
    // handle event:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID;
    pullrequestEventHandler.handlePullrequestEvent(new PullRequestEvent(Action.OPENED, pullrequest));

    // verify PR is stored with state CLOSED and correct id:
    verify(pullrequestService).findById(PR_ID);
    verify(pullrequestService).insertOrUpdate(pullrequestCaptor.capture());
    assertEquals(State.CLOSED, pullrequestCaptor.getValue().state);
    assertEquals(PR_ID, pullrequestCaptor.getValue().id);
  }
  
  @Test
  public void handleReopenedEventWithExistingOpenPullrequest() {
    // assume the PR exists and has state OPEN:
    existingPullrequest.state = State.OPEN;
    when(pullrequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullrequest));
    
    // handle event:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID;
    pullrequestEventHandler.handlePullrequestEvent(new PullRequestEvent(Action.REOPENED, pullrequest));

    // verify PR is stored with state OPENED and correct id:
    verify(pullrequestService).findById(PR_ID);
    verify(pullrequestService).insertOrUpdate(pullrequestCaptor.capture());
    assertEquals(State.OPEN, pullrequestCaptor.getValue().state);
    assertEquals(PR_ID, pullrequestCaptor.getValue().id);
  }
  
  @Test
  public void handleReopenedEventWithExistingClosedPullrequest() {
    // assume the PR exists and has state CLOSED:
    existingPullrequest.state = State.CLOSED;
    when(pullrequestService.findById(PR_ID)).thenReturn(Optional.of(existingPullrequest));
    
    // handle event:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID;
    pullrequestEventHandler.handlePullrequestEvent(new PullRequestEvent(Action.REOPENED, pullrequest));

    // verify PR is stored with state OPENED and correct id:
    verify(pullrequestService).findById(PR_ID);
    verify(pullrequestService).insertOrUpdate(pullrequestCaptor.capture());
    assertEquals(State.OPEN, pullrequestCaptor.getValue().state);
    assertEquals(PR_ID, pullrequestCaptor.getValue().id);
  }
}

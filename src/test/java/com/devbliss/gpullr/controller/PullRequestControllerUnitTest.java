package com.devbliss.gpullr.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

import com.devbliss.gpullr.controller.dto.PullRequestConverter;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Exceptionally, this controller is unit tested to verify the parsing logic.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestControllerUnitTest {

  @Mock
  private PullRequestService pullRequestService;

  @Mock
  private PullRequestConverter pullRequestConverter;

  @Mock
  private UserService userService;

  private PullRequestController pullRequestController;

  @Before
  public void setup() {
    pullRequestController = new PullRequestController(pullRequestService, pullRequestConverter, userService);
  }

  @Test
  public void getOpenPullRequestsWithReposFilterNull() {
    pullRequestController.findAllOpen(null);
    verify(pullRequestService).findAllOpen();
    verify(pullRequestService, never()).findAllOpen(any());
  }

  @Test
  public void getOpenPullRequestsWithReposFilterEmptyString() {
    pullRequestController.findAllOpen("");
    verify(pullRequestService).findAllOpen();
    verify(pullRequestService, never()).findAllOpen(any());
  }

  @Test
  public void getOpenPullRequestsWithReposFilterOneElement() {
    final String id = "1234";
    pullRequestController.findAllOpen(id);
    verify(pullRequestService, never()).findAllOpen();
    verify(pullRequestService).findAllOpen(id);
  }

  @Test
  public void getOpenPullRequestsWithReposFilterMultipleElements() {
    final String id = "789";
    final String name = "bla";
    pullRequestController.findAllOpen(String.format("%s;%s", id, name));
    verify(pullRequestService, never()).findAllOpen();
    verify(pullRequestService).findAllOpen(id, name);
  }
  
  @Test
  public void getOpenPullRequestsWithReposFilterMultipleElementsAndSemicolonInTheEnd() {
    final String id = "789";
    final String name = "bla";
    pullRequestController.findAllOpen(String.format("%s;%s;", id, name));
    verify(pullRequestService, never()).findAllOpen();
    verify(pullRequestService).findAllOpen(id, name);
  }

}

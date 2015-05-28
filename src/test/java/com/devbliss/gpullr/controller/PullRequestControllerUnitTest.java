package com.devbliss.gpullr.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.controller.dto.PullRequestConverter;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Exceptionally, this controller is unit tested to verify the parsing logic.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
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

  private List<PullRequest> pullRequests;

  @Before
  public void setup() {
    pullRequests = Arrays.asList(mock(PullRequest.class), mock(PullRequest.class));
    pullRequestController = new PullRequestController(pullRequestService, pullRequestConverter, userService);
  }

  @Test
  public void getOpenPullRequestsWithReposFilterNull() {
    when(pullRequestService.findAllOpen(true)).thenReturn(pullRequests);
    pullRequestController.findAllOpen(null);

    verify(pullRequestService).findAllOpen(eq(true));
    pullRequests.forEach(pr -> verify(pullRequestConverter).toDto(pr));
  }

  @Test
  public void getOpenPullRequestsWithReposFilterEmptyString() {
    when(pullRequestService.findAllOpen(true)).thenReturn(pullRequests);
    pullRequestController.findAllOpen("");
    verify(pullRequestService).findAllOpen(anyBoolean());
    verify(pullRequestService, never()).findAllOpenFiltered(any());
    pullRequests.forEach(pr -> verify(pullRequestConverter).toDto(pr));
  }

  @Test
  public void getOpenPullRequestsWithReposFilterOneElement() {
    final String id = "1234";
    when(pullRequestService.findAllOpenFiltered(id)).thenReturn(pullRequests);
    pullRequestController.findAllOpen(id);
    verify(pullRequestService, never()).findAllOpen(anyBoolean());
    verify(pullRequestService).findAllOpenFiltered(id);
    pullRequests.forEach(pr -> verify(pullRequestConverter).toDto(pr));
  }

  @Test
  public void getOpenPullRequestsWithReposFilterMultipleElements() {
    final String id = "789";
    final String name = "bla";
    when(pullRequestService.findAllOpenFiltered(id, name)).thenReturn(pullRequests);
    pullRequestController.findAllOpen(String.format("%s;%s", id, name));
    verify(pullRequestService, never()).findAllOpen(anyBoolean());
    verify(pullRequestService).findAllOpenFiltered(id, name);
    pullRequests.forEach(pr -> verify(pullRequestConverter).toDto(pr));
  }

  @Test
  public void getOpenPullRequestsWithReposFilterMultipleElementsAndSemicolonInTheEnd() {
    final String id = "789";
    final String name = "bla";
    when(pullRequestService.findAllOpenFiltered(id, name)).thenReturn(pullRequests);
    pullRequestController.findAllOpen(String.format("%s;%s;", id, name));
    verify(pullRequestService, never()).findAllOpen(anyBoolean());
    verify(pullRequestService).findAllOpenFiltered(id, name);
    pullRequests.forEach(pr -> verify(pullRequestConverter).toDto(pr));
  }

}

package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests certain behavior of {@link PullRequestService} with mocked dependencies.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestServiceUnitTest {

  private static final Integer ID = 1981;

  private static final ZonedDateTime IN_THE_PAST = ZonedDateTime.now().minusDays(1);

  private static final Integer ASSIGNEE_ID = 12;

  private static final Integer ANOTHER_ASSIGNEE_ID = 18;

  @Mock
  private GithubApi githubApi;

  @Mock
  private PullRequestRepository pullRequestRepository;

  @Mock
  private UserRepository userRepository;

  private User assignee;

  private User anotherAssignee;

  @Mock
  private User author;

  @Captor
  private ArgumentCaptor<PullRequest> pullRequestCaptor;

  private PullRequest pullRequestFromLocalStorage;

  private PullRequest pullRequestFromGitHub;

  private PullRequestService pullRequestService;

  @Before
  public void setup() {
    assignee = new User();
    assignee.id = ASSIGNEE_ID;
    anotherAssignee = new User();
    anotherAssignee.id = ANOTHER_ASSIGNEE_ID;
    pullRequestService = new PullRequestService(pullRequestRepository, userRepository, githubApi);
    pullRequestFromLocalStorage = new PullRequest();
    pullRequestFromLocalStorage.id = ID;
    pullRequestFromGitHub = new PullRequest();
    pullRequestFromGitHub.id = ID;
    pullRequestFromGitHub.author = author;
  }

  @Test
  public void keepLocallyStoredAssigneeWhenAssigneeFromEventIsNull() {
    pullRequestFromLocalStorage.assignee = assignee;
    pullRequestFromLocalStorage.assignedAt = IN_THE_PAST;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(assignee, pullRequestCaptor.getValue().assignee);
    assertEquals(IN_THE_PAST, pullRequestCaptor.getValue().assignedAt);
  }

  @Test
  public void overwriteLocallyStoredAssigneeWhenAssigneeFromEventIsNotNull() {
    pullRequestFromLocalStorage.assignee = assignee;
    pullRequestFromLocalStorage.assignedAt = IN_THE_PAST;
    pullRequestFromGitHub.assignee = anotherAssignee;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(anotherAssignee, pullRequestCaptor.getValue().assignee);
    assertTrue(IN_THE_PAST.isBefore(pullRequestCaptor.getValue().assignedAt));
  }
}

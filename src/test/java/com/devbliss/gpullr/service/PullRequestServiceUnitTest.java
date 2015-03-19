package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
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
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestServiceUnitTest {

  private static final Integer ID = 1981;

  private static final ZonedDateTime ONE_DAY_AGO = ZonedDateTime.now().minusDays(1);

  private static final ZonedDateTime FIVE_MINUTES_AGO = ZonedDateTime.now().minusMinutes(5);

  private static final Integer ASSIGNEE_ID = 12;

  private static final Integer ANOTHER_ASSIGNEE_ID = 18;

  private static final String OLD_PR_TITLE = "old";

  private static final String NEW_PR_TITLE = "new";

  @Mock
  private GithubApi githubApi;

  @Mock
  private PullRequestRepository pullRequestRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserService userService;

  private User assignee;

  private User anotherAssignee;

  @Mock
  private User author;

  @Mock
  private Repo repo;

  @Mock
  private RepoRepository repoRepository;

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
    pullRequestService = new PullRequestService(pullRequestRepository, userRepository, githubApi, userService,
        repoRepository);
    pullRequestFromLocalStorage = new PullRequest();
    pullRequestFromLocalStorage.id = ID;
    pullRequestFromLocalStorage.repo = repo;
    pullRequestFromGitHub = new PullRequest();
    pullRequestFromGitHub.id = ID;
    pullRequestFromGitHub.author = author;
    pullRequestFromGitHub.createdAt = ZonedDateTime.now();
    pullRequestFromGitHub.repo = repo;
  }

  @Test
  public void keepLocallyStoredAssigneeWhenAssigneeFromEventIsNull() {
    pullRequestFromLocalStorage.assignee = assignee;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(assignee, pullRequestCaptor.getValue().assignee);
  }

  @Test
  public void overwriteLocallyStoredAssigneeWhenAssigneeFromEventIsNotNull() {
    pullRequestFromLocalStorage.assignee = assignee;
    pullRequestFromGitHub.assignee = anotherAssignee;
    pullRequestFromGitHub.assignedAt = ZonedDateTime.now();
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(anotherAssignee, pullRequestCaptor.getValue().assignee);
  }

  @Test
  public void keepLocallyStoredAssignedAtWhenAssignedAtFromEventIsNull() {
    pullRequestFromLocalStorage.assignedAt = ONE_DAY_AGO;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(ONE_DAY_AGO, pullRequestCaptor.getValue().assignedAt);
  }

  @Test
  public void overwriteLocallyStoredAssignedAtWhenAssignedAtFromEventIsNotNull() {
    pullRequestFromLocalStorage.assignedAt = ONE_DAY_AGO;
    pullRequestFromGitHub.assignedAt = FIVE_MINUTES_AGO;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(FIVE_MINUTES_AGO, pullRequestCaptor.getValue().assignedAt);
  }

  @Test
  public void keepLocallyStoredClosedAtWhenClosedAtFromEventIsNull() {
    pullRequestFromLocalStorage.closedAt = ONE_DAY_AGO;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(ONE_DAY_AGO, pullRequestCaptor.getValue().closedAt);
  }

  @Test
  public void overwriteLocallyStoredClosedAtWhenClosedAtFromEventIsNotNull() {
    pullRequestFromLocalStorage.closedAt = ONE_DAY_AGO;
    pullRequestFromGitHub.closedAt = FIVE_MINUTES_AGO;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(FIVE_MINUTES_AGO, pullRequestCaptor.getValue().closedAt);
  }

  @Test
  public void keepLocallyStoredStateWhenStateFromEventIsNull() {
    pullRequestFromLocalStorage.state = State.OPEN;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(State.OPEN, pullRequestCaptor.getValue().state);
  }

  @Test
  public void overwriteLocallyStoredStateWhenStateFromEventIsNotNull() {
    pullRequestFromLocalStorage.state = State.OPEN;
    pullRequestFromGitHub.state = State.CLOSED;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(State.CLOSED, pullRequestCaptor.getValue().state);
  }

  @Test
  public void keepLocallyStoredRepoWhenRepoFromEventIsNull() {
    pullRequestFromLocalStorage.repo = repo;
    pullRequestFromGitHub.repo = null;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(repo, pullRequestCaptor.getValue().repo);
  }

  @Test
  public void overwriteLocallyStoredRepoWhenRepoFromEventIsNotNull() {
    Repo anotherRepo = mock(Repo.class);
    pullRequestFromLocalStorage.repo = repo;
    pullRequestFromGitHub.repo = anotherRepo;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertEquals(anotherRepo, pullRequestCaptor.getValue().repo);
  }

  @Test
  public void setNowAsClosedDateInAClosedPullRequestWhenThereIsNone() {
    pullRequestFromLocalStorage.closedAt = null;
    pullRequestFromGitHub.closedAt = null;
    pullRequestFromGitHub.state = State.CLOSED;
    when(pullRequestRepository.findById(ID)).thenReturn(Optional.of(pullRequestFromLocalStorage));
    pullRequestService.insertOrUpdate(pullRequestFromGitHub);
    verify(pullRequestRepository).save(pullRequestCaptor.capture());
    assertNotNull(pullRequestCaptor.getValue().closedAt);

    // close date is supposed to be about now:
    assertTrue(pullRequestCaptor.getValue().closedAt.isBefore(ZonedDateTime.now().plusSeconds(2)));
    assertTrue(pullRequestCaptor.getValue().closedAt.isAfter(ZonedDateTime.now().minusSeconds(2)));
  }

  @Test
  public void findAllOpenRegardsUserOrderOptions() {
    mockFindOpenPullRequests();
    when(userService.getCurrentUserIfLoggedIn()).thenReturn(Optional.of(author));

    List<PullRequest> allOpen = pullRequestService.findAllOpen();

    verify(userService).getCurrentUserIfLoggedIn();
    assertEquals(NEW_PR_TITLE, allOpen.get(0).title);
  }

  private void mockFindOpenPullRequests() {
    PullRequest oldPr = new PullRequest();
    oldPr.id = 1;
    oldPr.title = OLD_PR_TITLE;
    oldPr.createdAt = ZonedDateTime.now().minus(5, ChronoUnit.HOURS);

    PullRequest newPr = new PullRequest();
    newPr.id = 2;
    newPr.title = NEW_PR_TITLE;
    newPr.createdAt = ZonedDateTime.now();

    when(pullRequestRepository.findAllByState(eq(PullRequest.State.OPEN)))
      .thenReturn(Arrays.asList(newPr, oldPr));
  }
}

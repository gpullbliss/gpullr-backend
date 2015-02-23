package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests for {@link PullRequestService}
 *
 * @author Philipp Karstedt <philipp.karstedt@devbliss.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class PullRequestServiceTest {

  private static final int REPO_ID = 1000;

  private static final String REPO_NAME = "pr_test_repo";

  private static final String REPO_DESC = "pr_test_repo_description";

  private static final String USER_NAME = "pr_test_user";

  private static final String USER_NAME_2 = USER_NAME + "_2";

  private static final String AVATAR = "0815.jpg";

  private static final String AVATAR_2 = "what.ever.jpg";

  private static final int USER_ID = 1000;

  private static final int PR_ID = 1;

  @Autowired
  private PullRequestRepository prRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RepoRepository repoRepository;

  // @Autowired
  private GithubApi githubApi;

  private PullRequestService prService;

  private PullRequest testPr;

  @Before
  public void setup() {
    githubApi = mock(GithubApi.class);
    prService = new PullRequestService(prRepository, userRepository, githubApi);
    testPr = new PullRequest();
    testPr.id = PR_ID;
    testPr.owner = initUser();
    testPr.repo = initRepo();
    testPr.state = PullRequest.State.OPEN;
    testPr.createdAt = ZonedDateTime.now();
  }

  @After
  public void teardown() {
    prRepository.deleteAll();
    repoRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void insertOrupdatePullrequest() {
    // first of all check that no Pullrequest exists
    List<PullRequest> prs = prService.findAll();
    assertEquals(0, prs.size());

    prService.insertOrUpdate(testPr);
    prs = prService.findAll();
    assertEquals(1, prs.size());
    int fetchedPrId = prs.get(0).id;
    int fetchedPrUserId = prs.get(0).owner.id;
    int fetchedPrRepoId = prs.get(0).repo.id;

    assertEquals(PR_ID, fetchedPrId);
    assertEquals(USER_ID, fetchedPrUserId);
    assertEquals(REPO_ID, fetchedPrRepoId);
    assertEquals(testPr.state, prs.get(0).state);
  }

  @Test
  public void findAllOpenPullrequests() {
    // store a pullrequest with state OPEN:
    prService.insertOrUpdate(testPr);

    // store another with state CLOSED:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID + 1;
    pullrequest.repo = testPr.repo;
    pullrequest.owner = testPr.owner;
    pullrequest.state = State.CLOSED;
    pullrequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullrequest);

    // make sure only the open PR is returned:
    List<PullRequest> openPrs = prService.findAllOpen();
    assertEquals(1, openPrs.size());
    assertEquals(State.OPEN, openPrs.get(0).state);
    assertEquals(PR_ID, openPrs.get(0).id.intValue());
  }

  @Test
  public void assignPullrequest() {
    // create new PR w/o owner:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID + 1;
    pullrequest.repo = testPr.repo;
    pullrequest.state = State.OPEN;
    pullrequest.owner = testPr.owner;
    pullrequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullrequest);

    // assign to an existing user:
    User assignee = new User(USER_ID + 1, USER_NAME_2, AVATAR_2);
    userRepository.save(assignee);
    prService.assignPullrequest(assignee, pullrequest.id);

    // verify GitHub-API is called:
    verify(githubApi).assingUserToPullRequest(assignee, pullrequest);

    // verify the assignee is stored in our database as well:
    assertEquals(assignee, prService.findById(pullrequest.id).get().assignee);
  }

  @Test(expected = NotFoundException.class)
  public void assigningPullrequestToUnknownUserFails() {
    // create new PR w/o owner:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID + 1;
    pullrequest.repo = testPr.repo;
    pullrequest.state = State.OPEN;
    pullrequest.owner = testPr.owner;
    pullrequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullrequest);

    // assign to a non existing user:
    User assignee = new User(USER_ID + 1, USER_NAME_2, AVATAR_2);
    prService.assignPullrequest(assignee, pullrequest.id);
  }

  @Test
  public void findById() {
    // create a pull request:
    PullRequest pullrequest = new PullRequest();
    pullrequest.id = PR_ID + 1;
    pullrequest.repo = testPr.repo;
    pullrequest.state = State.CLOSED;
    pullrequest.owner = testPr.owner;
    pullrequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullrequest);

    // verify it can be fetched by id:
    Optional<PullRequest> fetched = prService.findById(pullrequest.id);
    assertTrue(fetched.isPresent());
    assertEquals(pullrequest, fetched.get());
  }

  private Repo initRepo() {
    Repo repo = new Repo(REPO_ID, REPO_NAME, REPO_DESC);
    return repoRepository.save(repo);
  }

  private User initUser() {
    User prOwner = new User(USER_ID, USER_NAME, AVATAR);
    return userRepository.save(prOwner);
  }
}

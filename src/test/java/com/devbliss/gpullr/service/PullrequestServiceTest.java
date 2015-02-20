package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.Pullrequest.State;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullrequestRepository;
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
 * Tests for {@link PullrequestService}
 *
 * @author Philipp Karstedt <philipp.karstedt@devbliss.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class PullrequestServiceTest {

  private static int REPO_ID = 1000;

  private static String REPO_NAME = "pr_test_repo";

  private static String REPO_DESC = "pr_test_repo_description";

  private static String USER_NAME = "pr_test_user";

  private static int USER_ID = 1000;

  private static int PR_ID = 1;

  @Autowired
  private PullrequestRepository prRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RepoRepository repoRepository;

  // @Autowired
  private GithubApi githubApi;

  private PullrequestService prService;

  private Pullrequest testPr;

  @Before
  public void setup() {
    githubApi = mock(GithubApi.class);
    prService = new PullrequestService(prRepository, userRepository, githubApi);
    testPr = new Pullrequest();
    testPr.id = PR_ID;
    testPr.owner = initUser();
    testPr.repo = initRepo();
    testPr.state = Pullrequest.State.OPEN;
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
    List<Pullrequest> prs = prService.findAll();
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
    Pullrequest pullrequest = new Pullrequest();
    pullrequest.id = PR_ID + 1;
    pullrequest.repo = testPr.repo;
    pullrequest.owner = testPr.owner;
    pullrequest.state = State.CLOSED;
    pullrequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullrequest);

    // make sure only the open PR is returned:
    List<Pullrequest> openPrs = prService.findAllOpen();
    assertEquals(1, openPrs.size());
    assertEquals(State.OPEN, openPrs.get(0).state);
    assertEquals(PR_ID, openPrs.get(0).id.intValue());
  }

  @Test
  public void assignPullrequest() {
    // create new PR w/o owner:
    Pullrequest pullrequest = new Pullrequest();
    pullrequest.id = PR_ID + 1;
    pullrequest.repo = testPr.repo;
    pullrequest.state = State.OPEN;
    pullrequest.owner = testPr.owner;
    pullrequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullrequest);

    // assign someone:
    User assignee = new User(USER_ID + 1, USER_NAME + "_2", "what.ever.jpg");
    prService.assignPullrequest(assignee, pullrequest.id);

    verify(githubApi).assingUserToPullRequest(assignee, pullrequest);
  }

  @Test
  public void findById() {
    // create a pull request:
    Pullrequest pullrequest = new Pullrequest();
    pullrequest.id = PR_ID + 1;
    pullrequest.repo = testPr.repo;
    pullrequest.state = State.CLOSED;
    pullrequest.owner = testPr.owner;
    pullrequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullrequest);

    // verify it can be fetched by id:
    Optional<Pullrequest> fetched = prService.findById(pullrequest.id);
    assertTrue(fetched.isPresent());
    assertEquals(pullrequest, fetched.get().id);
  }

  private Repo initRepo() {
    Repo repo = new Repo(REPO_ID, REPO_NAME, REPO_DESC);
    return repoRepository.save(repo);
  }

  private User initUser() {
    User prOwner = new User(USER_ID, USER_NAME, "0815.jpg");
    return userRepository.save(prOwner);
  }
}

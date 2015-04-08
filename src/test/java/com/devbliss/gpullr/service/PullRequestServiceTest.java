package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.BuildStatus;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

  private static final String FULL_NAME = "Test User";

  private static final String FULL_NAME_2 = FULL_NAME + " 2";

  private static final String AVATAR = "0815.jpg";

  private static final String AVATAR_2 = "what.ever.jpg";

  private static final String PROFILE_URL = "http://link.to.my.profile.example.com";

  private static final String PROFILE_URL_2 = "http://link.to.my.profile2.example.com";

  private static final int USER_ID = 1000;

  private static final int PR_ID = 1;

  private static final int BLACKLISTED_REPO_ID = 501231;

  private static final int OLD_PR_ID = 2;

  private static final String BRANCH_NAME = "feature/somethingReallyCool";

  private static final BuildStatus.State BUILD_STATUS = BuildStatus.State.PENDING;

  private static final ZonedDateTime BUILD_STATUS_TIMESTAMP = ZonedDateTime.now().minusMinutes(10);

  private static final int NUMBER_OF_COMMENTS = 17;

  @Autowired
  private PullRequestRepository prRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RepoRepository repoRepository;

  @Autowired
  private UserService userService;

  private GithubApi githubApi;

  private PullRequestService prService;

  private PullRequest testPr;

  @Before
  public void setup() {
    githubApi = mock(GithubApi.class);
    prService = new PullRequestService(prRepository, userRepository, githubApi, userService, repoRepository);
    testPr = new PullRequest();
    testPr.id = PR_ID;
    testPr.author = initUser();
    testPr.repo = initRepo();
    testPr.state = PullRequest.State.OPEN;
    testPr.createdAt = ZonedDateTime.now();
    testPr.branchName = BRANCH_NAME;
    testPr.buildStatus = new BuildStatus(BUILD_STATUS, BUILD_STATUS_TIMESTAMP, null);
    testPr.numberOfComments = NUMBER_OF_COMMENTS;
  }

  @After
  public void teardown() {
    prRepository.deleteAll();
    repoRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void insertOrUpdatePullRequest() {
    // first of all check that no pullRequest exists
    List<PullRequest> prs = prService.findAll();
    assertEquals(0, prs.size());

    prService.insertOrUpdate(testPr);
    prs = prService.findAll();
    assertEquals(1, prs.size());
    PullRequest fetched = prs.get(0);

    assertEquals(PR_ID, fetched.id.intValue());
    assertEquals(USER_ID, fetched.author.id.intValue());
    assertEquals(REPO_ID, fetched.repo.id.intValue());
    assertEquals(testPr.state, fetched.state);
    assertEquals(BUILD_STATUS, fetched.buildStatus.state);
    assertEquals(BUILD_STATUS_TIMESTAMP, fetched.buildStatus.timestamp);
    assertEquals(BRANCH_NAME, fetched.branchName);
    assertEquals(NUMBER_OF_COMMENTS, fetched.numberOfComments);
  }

  @Test
  public void savePullRequestStatus() {
    // create a pull request:
    prService.insertOrUpdate(testPr);

    // make sure it initially has the build status as expected:
    List<PullRequest> fetchedList = prService.findAll();
    assertEquals(BUILD_STATUS, fetchedList.get(0).buildStatus.state);
    assertEquals(BUILD_STATUS_TIMESTAMP, fetchedList.get(0).buildStatus.timestamp);

    // save new build status:
    final BuildStatus.State state = BuildStatus.State.SUCCESS;
    final ZonedDateTime timestamp = BUILD_STATUS_TIMESTAMP.plusMinutes(5);
    final int pullRequestId = fetchedList.get(0).id;
    BuildStatus status = new BuildStatus(state, timestamp, null);
    prService.saveBuildstatus(pullRequestId, status);

    // verify change:
    fetchedList = prService.findAll();
    assertEquals(state, fetchedList.get(0).buildStatus.state);
    assertEquals(timestamp, fetchedList.get(0).buildStatus.timestamp);
  }

  @Test
  public void findAllOpenPullRequests() {
    User user = initUser();
    userService.login(user.id);

    // store a pullRequest with state OPEN:
    prService.insertOrUpdate(testPr);

    // store another with state CLOSED:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID + 1;
    pullRequest.repo = testPr.repo;
    pullRequest.author = testPr.author;
    pullRequest.state = State.CLOSED;
    pullRequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullRequest);

    // make sure only the open PR is returned:
    List<PullRequest> openPrs = prService.findAllOpen();
    assertEquals(1, openPrs.size());
    assertEquals(State.OPEN, openPrs.get(0).state);
    assertEquals(PR_ID, openPrs.get(0).id.intValue());
  }

  @Test
  public void findAllOpenPullRequestsByIdsOrNames() {

    // create some more open and a closed pull request with different repos:
    final int repo1Id = 99;
    PullRequest openPullRequest1 = new PullRequest();
    openPullRequest1.id = PR_ID + 7;
    openPullRequest1.author = testPr.author;
    openPullRequest1.state = State.OPEN;
    openPullRequest1.createdAt = ZonedDateTime.now().minusMinutes(3);
    openPullRequest1.repo = repoRepository.save(new Repo(repo1Id, "One Repo", ""));
    prService.insertOrUpdate(openPullRequest1);

    final String repo2Title = "My Cool Repo";
    PullRequest openPullRequest2 = new PullRequest();
    openPullRequest2.id = PR_ID + 8;
    openPullRequest2.author = testPr.author;
    openPullRequest2.state = State.OPEN;
    openPullRequest2.createdAt = ZonedDateTime.now().minusMinutes(3);
    openPullRequest2.repo = repoRepository.save(new Repo(repo1Id + 1, repo2Title, ""));
    prService.insertOrUpdate(openPullRequest2);

    final String repo3Title = "999888777";
    PullRequest openPullRequest3 = new PullRequest();
    openPullRequest3.id = PR_ID + 9;
    openPullRequest3.author = testPr.author;
    openPullRequest3.state = State.OPEN;
    openPullRequest3.createdAt = ZonedDateTime.now().minusMinutes(3);
    openPullRequest3.repo = repoRepository.save(new Repo(repo1Id + 2, repo3Title, ""));
    prService.insertOrUpdate(openPullRequest3);

    final int repo4Id = 250;
    PullRequest closedPullRequest = new PullRequest();
    closedPullRequest.id = PR_ID + 10;
    closedPullRequest.author = testPr.author;
    closedPullRequest.state = State.CLOSED;
    closedPullRequest.createdAt = ZonedDateTime.now().minusMinutes(3);
    closedPullRequest.repo = repoRepository.save(new Repo(repo4Id, "Another Repo", ""));
    prService.insertOrUpdate(closedPullRequest);

    // expecting to retrieve three pull requests:
    List<PullRequest> pullRequests = prService.findAllOpen(repo2Title,
        Integer.toString(repo1Id),
        repo3Title,
        Integer.toString(repo4Id));
    assertEquals(3, pullRequests.size());
    assertTrue(pullRequests.contains(openPullRequest1));
    assertTrue(pullRequests.contains(openPullRequest2));
    assertTrue(pullRequests.contains(openPullRequest3));
  }

  @Test(expected = NotFoundException.class)
  public void findAllOpenPullRequestsByIdsOrNamesFailsOnUnknownRepoId() {

    // create one pull request and a repo:
    final int repoId = 99;
    PullRequest openPullRequest = new PullRequest();
    openPullRequest.id = PR_ID + 7;
    openPullRequest.author = testPr.author;
    openPullRequest.state = State.OPEN;
    openPullRequest.createdAt = ZonedDateTime.now().minusMinutes(3);
    openPullRequest.repo = repoRepository.save(new Repo(repoId, "One Repo", ""));
    prService.insertOrUpdate(openPullRequest);

    // ask for all PRs with the id of the repo just created or a non-existing id:
    prService.findAllOpen(Integer.toString(repoId), Integer.toString(repoId + 1));
  }

  @Test(expected = NotFoundException.class)
  public void findAllOpenPullRequestsByIdsOrNamesFailsOnUnknownRepoTitle() {
    // create one pull request and a repo:
    final String repoTitle = "My Cool Repo";
    PullRequest openPullRequest = new PullRequest();
    openPullRequest.id = PR_ID + 8;
    openPullRequest.author = testPr.author;
    openPullRequest.state = State.OPEN;
    openPullRequest.createdAt = ZonedDateTime.now().minusMinutes(3);
    openPullRequest.repo = repoRepository.save(new Repo(1997 + 1, repoTitle, ""));
    prService.insertOrUpdate(openPullRequest);

    // ask for all PRs with the title of the repo just created or a non-existing title:
    prService.findAllOpen(repoTitle, repoTitle + "_doesnotexist");
  }

  @Test
  public void findAllOpenPullRequestsRegardsUserOrderSettings() {
    User user = initUser();
    userService.login(user.id);

    user.userSettings = new UserSettings(UserSettings.OrderOption.DESC);
    userService.insertOrUpdate(user);

    // create pull request:
    prService.insertOrUpdate(testPr);

    // create second pull request that is OLDER:
    testPr.id = OLD_PR_ID;
    testPr.url = testPr.url + "_2";
    testPr.createdAt = testPr.createdAt.minus(1, ChronoUnit.HOURS);
    prService.insertOrUpdate(testPr);

    // since user has no user preferences yet, the newer pull request should be first in list
    // (default behavior):
    List<PullRequest> allOpen = prService.findAllOpen();
    assertEquals(PR_ID, allOpen.get(0).id.intValue());

    // after storing user preference that user wants pull requests in ascending order, the other one
    // should be first in list:
    user.userSettings.defaultPullRequestListOrdering = UserSettings.OrderOption.ASC;
    userService.updateUserSettings(user.id, user.userSettings);
    userService.updateUserSession(user);
    allOpen = prService.findAllOpen();
    assertEquals(OLD_PR_ID, allOpen.get(0).id.intValue());

    // after changing user preference to descending order, the order changes again:
    user.userSettings.defaultPullRequestListOrdering = UserSettings.OrderOption.DESC;
    userService.updateUserSettings(user.id, user.userSettings);
    userService.updateUserSession(user);
    allOpen = prService.findAllOpen();
    assertEquals(PR_ID, allOpen.get(0).id.intValue());
  }

  @Test
  public void regardsUserBlacklist() {
    User user = initUser();
    user.userSettings = new UserSettings(UserSettings.OrderOption.DESC, Arrays.asList(BLACKLISTED_REPO_ID));
    userService.insertOrUpdate(user);
    userService.login(user.id);
    userService.updateUserSession(user);

    // create pr that will be filtered, because its repo is blacklisted
    prService.insertOrUpdate(testPr);

    // create pr that will _not_ be filtered
    testPr.id = 500000;
    testPr.repo = initRepo(BLACKLISTED_REPO_ID, "another repo");
    prService.insertOrUpdate(testPr);

    List<PullRequest> allOpen = prService.findAllOpen();

    assertEquals(1, allOpen.size());
    assertEquals(PR_ID, (int) allOpen.get(0).id);
  }

  @Test
  public void assignPullRequest() {
    // create new PR w/o owner:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID + 1;
    pullRequest.repo = testPr.repo;
    pullRequest.state = State.OPEN;
    pullRequest.author = testPr.author;
    pullRequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullRequest);

    // assign to an existing user:
    User assignee = new User(USER_ID + 1, USER_NAME_2, FULL_NAME_2, AVATAR_2, PROFILE_URL);
    userRepository.save(assignee);
    prService.assignPullRequest(assignee, pullRequest.id);

    // verify GitHub-API is called:
    verify(githubApi).assignUserToPullRequest(assignee, pullRequest);

    // verify the assignee is stored in our database as well:
    assertEquals(assignee, prService.findById(pullRequest.id).get().assignee);
  }

  @Test(expected = NotFoundException.class)
  public void assigningPullRequestToUnknownUserFails() {
    // create new PR w/o owner:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID + 1;
    pullRequest.repo = testPr.repo;
    pullRequest.state = State.OPEN;
    pullRequest.author = testPr.author;
    pullRequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullRequest);

    // assign to a non existing user:
    User assignee = new User(USER_ID + 1, USER_NAME_2, FULL_NAME_2, AVATAR_2, PROFILE_URL_2);
    prService.assignPullRequest(assignee, pullRequest.id);
  }

  @Test
  public void unassignPullRequest() {
    // create new PR w/o owner:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID + 3121;
    pullRequest.repo = testPr.repo;
    pullRequest.state = State.OPEN;
    pullRequest.author = testPr.author;
    pullRequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullRequest);

    // assign to an existing user:
    User assignee = new User(USER_ID + 4564, USER_NAME_2);
    userRepository.save(assignee);
    prService.assignPullRequest(assignee, pullRequest.id);
    prService.unassignPullRequest(assignee, pullRequest.id);

    // verify GitHub-API is called:
    verify(githubApi).unassignUserFromPullRequest(assignee, pullRequest);
  }

  @Test(expected = NotFoundException.class)
  public void unassignUnknownPullRequestFails() {
    User user = new User(USER_ID, USER_NAME_2);

    prService.unassignPullRequest(user, 1);
  }

  @Test(expected = NotFoundException.class)
  public void unassignUnknownUserFails() {
    User user = new User(USER_ID + 1, USER_NAME_2);
    prService.unassignPullRequest(user, PR_ID);
  }

  @Test
  public void findById() {
    // create a pull request:
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = PR_ID + 1;
    pullRequest.repo = testPr.repo;
    pullRequest.state = State.CLOSED;
    pullRequest.author = testPr.author;
    pullRequest.createdAt = ZonedDateTime.now();
    prService.insertOrUpdate(pullRequest);

    // verify it can be fetched by id:
    Optional<PullRequest> fetched = prService.findById(pullRequest.id);
    assertTrue(fetched.isPresent());
    assertEquals(pullRequest, fetched.get());
  }

  private Repo initRepo() {
    return initRepo(null, null);
  }

  private Repo initRepo(Integer repoId, String repoName) {
    Repo repo = new Repo(REPO_ID, REPO_NAME, REPO_DESC);

    if (repoId != null) {
      repo.id = repoId;
    }
    if (repoName != null) {
      repo.name = repoName;
    }

    return repoRepository.save(repo);
  }

  private User initUser() {
    User prOwner = new User(USER_ID, USER_NAME, FULL_NAME, AVATAR, PROFILE_URL);
    return userRepository.save(prOwner);
  }
}

package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullrequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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

  private PullrequestService prService;

  private Pullrequest testPr;

  @Before
  public void setup() {
    prService = new PullrequestService(prRepository, userRepository);
    testPr = new Pullrequest();
    testPr.id = PR_ID;
    testPr.owner = initUser();
    testPr.repo = initRepo();
    testPr.state = Pullrequest.State.OPEN;
    testPr.createdAt = ZonedDateTime.now();
  }

  @Test
  public void insertOrupdatePullrequest() {
    // first of all check that no Pullrequest exists
    List<Pullrequest> prs = prService.findAll();
    assertEquals(0, prs.size());

    prService.insertOrUpdate(testPr);
    prs = prService.findAll();
    assertEquals(1, prs.size());
    int fetched_PR_id = prs.get(0).id;
    int fetched_PR_User_id = prs.get(0).owner.id;
    int fetched_PR_Repo_id = prs.get(0).repo.id;

    assertEquals(PR_ID, fetched_PR_id);
    assertEquals(USER_ID, fetched_PR_User_id);
    assertEquals(REPO_ID, fetched_PR_Repo_id);
    assertEquals(testPr.state, prs.get(0).state);
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

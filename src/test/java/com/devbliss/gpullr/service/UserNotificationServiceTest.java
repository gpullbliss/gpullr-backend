package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequestComment;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.notifications.PullRequestClosedUserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotification;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullRequestCommentRepository;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
import com.devbliss.gpullr.repository.UserNotificationRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Notification Service Integration Test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class UserNotificationServiceTest {

  @Autowired
  private UserNotificationRepository userNotificationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RepoRepository repoRepository;

  @Autowired
  private PullRequestRepository pullRequestRepository;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private PullRequestCommentService pullRequestCommentService;

  @Autowired
  private PullRequestCommentRepository pullRequestCommentRepository;

  private UserNotificationService notificationService;

  private Repo repo;

  private User assignee;

  private User receivingUser;

  @Before
  public void setUp() throws Exception {
    repo = repoRepository.save(new Repo(0x1337, "mega repository name", "mega, I said."));
    assignee = userRepository.save(new User(1, "interested code reviewer"));
    receivingUser = userRepository.save(new User(2, "flying fingaz codr"));
    notificationService = new UserNotificationService(userNotificationRepository, applicationContext, pullRequestCommentRepository);
  }

  @After
  public void tearDown() throws Exception {
    pullRequestCommentRepository.deleteAll();
    userNotificationRepository.deleteAll();
    pullRequestRepository.deleteAll();
    userRepository.deleteAll();
    repoRepository.deleteAll();
  }

  @Test
  public void notificationsAvailable() {
    PullRequest pullRequest = createAndSaveClosedPullRequest(0xBABE, assignee, receivingUser,
        ZonedDateTime.now().plusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestClosedUserNotification);
    assertEquals(assignee.id, ((PullRequestClosedUserNotification) notifications.get(0)).actor.id);
    assertTrue(notifications.get(0).seen == false);
  }

  @Test
  public void markNotificationAsSeen() {
    PullRequest pullRequest = createAndSaveClosedPullRequest(0xBABE, assignee, receivingUser,
        ZonedDateTime.now().plusMinutes(1L));

    notificationService.createClosedPullRequestNotification(pullRequest);
    List<UserNotification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, allUnreadNotifications.size());

    notificationService.markAsSeen(allUnreadNotifications.get(0).id);
    allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, allUnreadNotifications.size());
  }

  @Test
  public void markAllNotificationsAsSeen() {
    PullRequest pullRequest;

    pullRequest = createAndSaveClosedPullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);
    pullRequest = createAndSaveClosedPullRequest(0xC002, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);
    pullRequest = createAndSaveClosedPullRequest(0xC003, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<UserNotification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(3, allUnreadNotifications.size());

    notificationService.markAsSeen(allUnreadNotifications.get(0).id);
    allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(2, allUnreadNotifications.size());

    notificationService.markAllAsSeenForUser(receivingUser.id);
    allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, allUnreadNotifications.size());
  }

  @Test
  public void markAllNotificationsAsSeenMultipleTimes() {
    PullRequest pullRequest;
    int prId1 = 0xC001;

    pullRequest = createAndSaveClosedPullRequest(prId1, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<UserNotification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, allUnreadNotifications.size());

    notificationService.markAllAsSeenForUser(receivingUser.id);
    List<UserNotification> unreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, unreadNotifications.size());

    notificationService.markAsSeen(allUnreadNotifications.get(0).id);
    unreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, unreadNotifications.size());
  }

  @Test
  public void oldEventsBeforeAppStartupNotEvaluated() {
    PullRequest pullRequest;

    pullRequest =
        createAndSaveClosedPullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().minusMinutes(10L));
    notificationService.createClosedPullRequestNotification(pullRequest);
    pullRequest =
        createAndSaveClosedPullRequest(0xC002, assignee, receivingUser, ZonedDateTime.now().minusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<UserNotification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, allUnreadNotifications.size());
  }

  @Test(expected = NotFoundException.class)
  public void markingNonexistentNotificationsResultsInException() {
    notificationService.markAsSeen(12345L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void pullRequestMustBeClosedToCreateClosedPullRequestNotification() {
    PullRequest pullRequest =
        createAndSaveClosedPullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().minusMinutes(10L));
    pullRequest.state = PullRequest.State.OPEN;
    notificationService.createClosedPullRequestNotification(pullRequest);
  }

  @Test
  public void calculateCommentNotification() {
    User author = receivingUser;

    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, notifications.size());

    PullRequest pullRequest = new PullRequest();
    pullRequest.id = 234;
    pullRequest.repo = repo;
    pullRequest.state = PullRequest.State.OPEN;
    pullRequest.assignee = assignee;
    pullRequest.author = receivingUser;

    pullRequestRepository.save(pullRequest);

    PullRequestComment pullRequestComment = new PullRequestComment();
    pullRequestComment.setId(123);
    pullRequestComment.setCreatedAt(ZonedDateTime.now().minusHours(1));
    pullRequestComment.setPullRequest(pullRequest);

    pullRequestCommentService.save(Arrays.asList(pullRequestComment));

    notificationService.calculateCommentNotifications();

    notifications = notificationService.allUnseenNotificationsForUser(author.id);
    assertEquals(1, notifications.size());

  }

  private PullRequest createAndSaveClosedPullRequest(Integer id,
      User assignee,
      User receivingUser,
      ZonedDateTime prCloseTime) {
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = id;
    pullRequest.repo = repo;
    pullRequest.assignee = assignee;
    pullRequest.author = receivingUser;
    pullRequest.closedAt = prCloseTime;
    pullRequest.state = PullRequest.State.CLOSED;
    pullRequest = pullRequestRepository.save(pullRequest);
    return pullRequest;
  }

}

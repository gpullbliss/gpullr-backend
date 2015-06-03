package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequestComment;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.notifications.PullRequestClosedUserNotification;
import com.devbliss.gpullr.domain.notifications.PullRequestCommentedUserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotificationType;
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
    notificationService = new UserNotificationService(userNotificationRepository, applicationContext,
        pullRequestCommentRepository);
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
    PullRequest pullRequest = createAndSavePullRequest(0xBABE, assignee, receivingUser,
        ZonedDateTime.now().plusMinutes(1L), PullRequest.State.CLOSED);
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestClosedUserNotification);
    assertEquals(assignee.id, ((PullRequestClosedUserNotification) notifications.get(0)).actor.id);
    assertTrue(notifications.get(0).seen == false);
  }

  @Test
  public void markNotificationAsSeen() {
    PullRequest pullRequest = createAndSavePullRequest(0xBABE, assignee, receivingUser,
        ZonedDateTime.now().plusMinutes(1L), PullRequest.State.CLOSED);

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

    pullRequest = createAndSavePullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L),
        PullRequest.State.CLOSED);
    notificationService.createClosedPullRequestNotification(pullRequest);
    pullRequest = createAndSavePullRequest(0xC002, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L),
        PullRequest.State.CLOSED);
    notificationService.createClosedPullRequestNotification(pullRequest);
    pullRequest = createAndSavePullRequest(0xC003, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L),
        PullRequest.State.CLOSED);
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

    pullRequest = createAndSavePullRequest(prId1, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L),
        PullRequest.State.CLOSED);
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
        createAndSavePullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().minusMinutes(10L),
            PullRequest.State.CLOSED);
    notificationService.createClosedPullRequestNotification(pullRequest);
    pullRequest =
        createAndSavePullRequest(0xC002, assignee, receivingUser, ZonedDateTime.now().minusMinutes(1L),
            PullRequest.State.CLOSED);
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
        createAndSavePullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().minusMinutes(10L),
            PullRequest.State.CLOSED);
    pullRequest.state = PullRequest.State.OPEN;
    notificationService.createClosedPullRequestNotification(pullRequest);
  }

  @Test
  public void calculateOneCommentNotificationForOneComment() {

    // at the beginning, there should be no notification:
    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, notifications.size());

    // creating one pullrequest and a comment for it:
    PullRequest pullRequest = createAndSavePullRequest(
        234,
        assignee,
        receivingUser,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);
    createAndSavePullRequestComment(123, pullRequest);

    // after triggering notification calculation, there should be one comment notification:
    notificationService.calculateCommentNotifications();

    notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestCommentedUserNotification);
    assertEquals(UserNotificationType.PULLREQUEST_COMMENTED, notifications.get(0).notificationType);
    PullRequestCommentedUserNotification notification = (PullRequestCommentedUserNotification) notifications.get(0);
    assertEquals(1, notification.count);
  }

  @Test
  public void calculateOneCommentNotificationForTwoCommentsOnSamePullRequest() {
    // at the beginning, there should be no notification:
    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, notifications.size());

    // creating one pullrequest and two comments for it:
    PullRequest pullRequest = createAndSavePullRequest(
        234,
        assignee,
        receivingUser,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);
    createAndSavePullRequestComment(123, pullRequest);
    createAndSavePullRequestComment(124, pullRequest);

    // after triggering notification calculation, there should be one comment notification:
    notificationService.calculateCommentNotifications();
    notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestCommentedUserNotification);
    assertEquals(UserNotificationType.PULLREQUEST_COMMENTED, notifications.get(0).notificationType);
    PullRequestCommentedUserNotification notification = (PullRequestCommentedUserNotification) notifications.get(0);
    assertEquals(2, notification.count);
  }

  @Test
  public void calculateTwoCommentNotificationForTwoCommentsOnSamePullRequestWhereOneIsSeen() {
    // at the beginning, there should be no notification:
    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, notifications.size());

    // creating one pullrequest and two comments for it and trigger notification calculation:
    PullRequest pullRequest = createAndSavePullRequest(
        234,
        assignee,
        receivingUser,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);
    createAndSavePullRequestComment(123, pullRequest);
    createAndSavePullRequestComment(124, pullRequest);
    notificationService.calculateCommentNotifications();

    // mark notification as seen:
    notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    notificationService.markAsSeen(notifications.get(0).id);

    // create another comment for the pull request:
    createAndSavePullRequestComment(125, pullRequest);

    // after triggering the notification calculation again, there should be a new unseen
    // notification:
    notificationService.calculateCommentNotifications();
    notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestCommentedUserNotification);
    assertEquals(UserNotificationType.PULLREQUEST_COMMENTED, notifications.get(0).notificationType);
    PullRequestCommentedUserNotification notification = (PullRequestCommentedUserNotification) notifications.get(0);
    assertEquals(1, notification.count);
  }

  private PullRequestComment createAndSavePullRequestComment(int id, PullRequest pullRequest) {
    PullRequestComment pullRequestComment = new PullRequestComment();
    pullRequestComment.setId(id);
    pullRequestComment.setCreatedAt(ZonedDateTime.now().minusHours(1));
    pullRequestComment.setPullRequest(pullRequest);
    pullRequestCommentService.save(Arrays.asList(pullRequestComment));
    return pullRequestComment;
  }

  private PullRequest createAndSavePullRequest(
      Integer id,
      User assignee,
      User receivingUser,
      ZonedDateTime prCloseTime,
      PullRequest.State state) {
    PullRequest pullRequest = new PullRequest();
    pullRequest.id = id;
    pullRequest.repo = repo;
    pullRequest.assignee = assignee;
    pullRequest.author = receivingUser;
    pullRequest.closedAt = prCloseTime;
    pullRequest.state = state;
    pullRequest = pullRequestRepository.save(pullRequest);
    return pullRequest;
  }

}

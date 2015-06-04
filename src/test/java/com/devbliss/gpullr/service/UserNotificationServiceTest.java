package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Comment;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.notifications.PullRequestClosedUserNotification;
import com.devbliss.gpullr.domain.notifications.PullRequestCommentedUserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotificationType;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.CommentRepository;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
import com.devbliss.gpullr.repository.UserNotificationRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
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
  private CommentService pullRequestCommentService;

  @Autowired
  private CommentRepository pullRequestCommentRepository;

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
    assertTrue(notificationService.allUnseenNotificationsForUser(receivingUser.id).isEmpty());

    // creating one pullrequest and a comment for it:
    PullRequest pullRequest = createAndSavePullRequest(
        234,
        assignee,
        receivingUser,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);
    createAndSaveComment(123, pullRequest);

    // after triggering notification calculation, there should be one comment notification for the
    // pullrequest author:
    notificationService.calculateCommentNotifications(pullRequest);

    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestCommentedUserNotification);
    assertEquals(UserNotificationType.PULLREQUEST_COMMENTED, notifications.get(0).notificationType);
    PullRequestCommentedUserNotification notification = (PullRequestCommentedUserNotification) notifications.get(0);
    assertEquals(1, notification.count);

    // but still none for the assignee:
    notifications = notificationService.allUnseenNotificationsForUser(assignee.id);
    assertEquals(0, notifications.size());
  }

  @Test
  public void calculateOneCommentNotificationForTwoCommentsOnSamePullRequest() {
    // at the beginning, there should be no notification:
    assertTrue(notificationService.allUnseenNotificationsForUser(receivingUser.id).isEmpty());

    // creating one pullrequest and two comments for it:
    PullRequest pullRequest = createAndSavePullRequest(
        234,
        assignee,
        receivingUser,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);
    createAndSaveComment(123, pullRequest);
    createAndSaveComment(124, pullRequest);

    // after triggering notification calculation, there should be one comment notification:
    notificationService.calculateCommentNotifications(pullRequest);
    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestCommentedUserNotification);
    assertEquals(UserNotificationType.PULLREQUEST_COMMENTED, notifications.get(0).notificationType);
    PullRequestCommentedUserNotification notification = (PullRequestCommentedUserNotification) notifications.get(0);
    assertEquals(2, notification.count);

    // but still none for the assignee:
    notifications = notificationService.allUnseenNotificationsForUser(assignee.id);
    assertEquals(0, notifications.size());
  }

  @Test
  public void calculateTwoCommentNotificationsForTwoCommentsOnSamePullRequestWhereOneIsSeen() {
    // at the beginning, there should be no notification:
    assertTrue(notificationService.allUnseenNotificationsForUser(receivingUser.id).isEmpty());

    // creating one pullrequest and two comments for it and trigger notification calculation:
    PullRequest pullRequest = createAndSavePullRequest(
        234,
        assignee,
        receivingUser,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);
    createAndSaveComment(123, pullRequest);
    createAndSaveComment(124, pullRequest);
    notificationService.calculateCommentNotifications(pullRequest);

    // mark notification as seen:
    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    notificationService.markAsSeen(notifications.get(0).id);

    // create another comment for the pull request:
    createAndSaveComment(125, pullRequest);

    // after triggering the notification calculation again, there should be a new unseen
    // notification:
    notificationService.calculateCommentNotifications(pullRequest);
    notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertTrue(notifications.get(0) instanceof PullRequestCommentedUserNotification);
    assertEquals(UserNotificationType.PULLREQUEST_COMMENTED, notifications.get(0).notificationType);
    PullRequestCommentedUserNotification notification = (PullRequestCommentedUserNotification) notifications.get(0);
    assertEquals(1, notification.count);

    // but still none for the assignee:
    notifications = notificationService.allUnseenNotificationsForUser(assignee.id);
    assertEquals(0, notifications.size());
  }

  @Test
  public void calculateTwoCommentNotificationsForFiveCommentsOnTwoDifferentPullRequests() {
    // at the beginning, there should be no notification for any user:
    assertTrue(notificationService.allUnseenNotificationsForUser(receivingUser.id).isEmpty());
    assertTrue(notificationService.allUnseenNotificationsForUser(assignee.id).isEmpty());

    // create two pull requests with assignee user as assignee receiving user as author and vice
    // versa:
    PullRequest pr0 = createAndSavePullRequest(
        234,
        assignee,
        receivingUser,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);

    PullRequest pr1 = createAndSavePullRequest(
        789,
        receivingUser,
        assignee,
        ZonedDateTime.now().minusHours(2),
        PullRequest.State.OPEN);

    // create two comments with one of the pull requests and three with the other:
    createAndSaveComment(123, pr0);
    createAndSaveComment(124, pr0);

    createAndSaveComment(125, pr1);
    createAndSaveComment(126, pr1);
    createAndSaveComment(127, pr1);

    // after triggering the notification calculation, there should be the respective notifications:
    notificationService.calculateCommentNotifications(pr0);
    notificationService.calculateCommentNotifications(pr1);
    List<UserNotification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertEquals(2, ((PullRequestCommentedUserNotification) notifications.get(0)).count);

    notifications = notificationService.allUnseenNotificationsForUser(assignee.id);
    assertEquals(1, notifications.size());
    assertEquals(3, ((PullRequestCommentedUserNotification) notifications.get(0)).count);
  }

  private Comment createAndSaveComment(int id, PullRequest pullRequest) {
    Comment pullRequestComment = new Comment();
    pullRequestComment.setId(id);
    pullRequestComment.setCreatedAt(ZonedDateTime.now().minusHours(1));
    pullRequestComment.setPullRequest(pullRequest);
    pullRequestCommentService.save(pullRequestComment);
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

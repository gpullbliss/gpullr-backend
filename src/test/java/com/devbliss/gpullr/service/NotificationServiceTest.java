package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.notifications.Notification;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.NotificationRepository;
import com.devbliss.gpullr.repository.PullRequestRepository;
import com.devbliss.gpullr.repository.RepoRepository;
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
public class NotificationServiceTest {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RepoRepository repoRepository;

  @Autowired
  private PullRequestRepository pullRequestRepository;

  @Autowired
  private ApplicationContext applicationContext;

  private NotificationService notificationService;

  private Repo repo;
  
  private User assignee;
  
  private User receivingUser;

  @Before
  public void setUp() throws Exception {
    repo = repoRepository.save(new Repo(0x1337, "mega repository name", "mega, I said."));
    assignee = userRepository.save(new User(1, "interested code reviewer"));
    receivingUser = userRepository.save(new User(2, "flying fingaz codr"));
    notificationService = new NotificationService(notificationRepository, applicationContext);
  }

  @After
  public void tearDown() throws Exception {
    notificationRepository.deleteAll();
    pullRequestRepository.deleteAll();
    userRepository.deleteAll();
    repoRepository.deleteAll();
  }

  @Test
  public void notificationsAvailable() {
    PullRequest pullRequest = createAndSaveClosedPullRequest(0xBABE, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<Notification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertEquals(assignee.id, notifications.get(0).actor.id);
    assertTrue(notifications.get(0).seen == false);
  }

  @Test
  public void markNotificationAsSeen() {
    PullRequest pullRequest = createAndSaveClosedPullRequest(0xBABE, assignee, receivingUser, ZonedDateTime.now().plusMinutes(1L));

    notificationService.createClosedPullRequestNotification(pullRequest);
    List<Notification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
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

    List<Notification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
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

    List<Notification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, allUnreadNotifications.size());

    notificationService.markAllAsSeenForUser(receivingUser.id);
    List<Notification> unreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, unreadNotifications.size());

    notificationService.markAsSeen(allUnreadNotifications.get(0).id);
    unreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, unreadNotifications.size());
  }

  @Test
  public void oldEventsBeforeAppStartupNotEvaluated() {
    PullRequest pullRequest;

    pullRequest = createAndSaveClosedPullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().minusMinutes(10L));
    notificationService.createClosedPullRequestNotification(pullRequest);
    pullRequest = createAndSaveClosedPullRequest(0xC002, assignee, receivingUser, ZonedDateTime.now().minusMinutes(1L));
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<Notification> allUnreadNotifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(0, allUnreadNotifications.size());
  }

  @Test(expected = NotFoundException.class)
  public void markingNonexistentNotificationsResultsInException() {
    notificationService.markAsSeen(12345L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void pullRequestMustBeClosedToCreateClosedPullRequestNotification() {
    PullRequest pullRequest = createAndSaveClosedPullRequest(0xC001, assignee, receivingUser, ZonedDateTime.now().minusMinutes(10L));
    pullRequest.state = PullRequest.State.OPEN;
    notificationService.createClosedPullRequestNotification(pullRequest);
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
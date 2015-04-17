package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.notifications.Notification;
import com.devbliss.gpullr.repository.NotificationRepository;
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
  private ApplicationContext applicationContext;

  private NotificationService notificationService;

  private Repo repo;
  private User assignee;
  private User receivingUser;

  @Before
  public void setUp() throws Exception {
    repo = repoRepository.save(new Repo(0x1337, "mega reponame", "mega, I said."));

    System.err.println("repo: " + repo.toString());

    assignee = userRepository.save(new User(1, "interested code reviewer"));
    receivingUser = userRepository.save(new User(2, "flying fingaz codr"));

    notificationService = new NotificationService(notificationRepository, applicationContext);
  }

  @After
  public void tearDown() throws Exception {
    notificationRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
//  public void markNotificationAsSeen(){
  public void notificationsAvailable() {
    PullRequest pullRequest = new PullRequest();
    pullRequest.assignee = assignee;
    pullRequest.author = receivingUser;
    pullRequest.closedAt = ZonedDateTime.now().plusMinutes(1L);
    notificationService.createClosedPullRequestNotification(pullRequest);

    List<Notification> notifications = notificationService.allUnseenNotificationsForUser(receivingUser.id);
    assertEquals(1, notifications.size());
    assertEquals(assignee.id, notifications.get(0).actor.id);
  }
}
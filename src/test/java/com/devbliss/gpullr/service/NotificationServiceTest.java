package com.devbliss.gpullr.service;

import static org.junit.Assert.*;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.NotificationRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.time.ZonedDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private ApplicationContext applicationContext;

  private NotificationService notificationService;

  private User assignee;
  private User receivingUser;

  @Before
  public void setUp() throws Exception {
    assignee = userRepository.save(new User());
    receivingUser = userRepository.save(new User());

    notificationService = new NotificationService(notificationRepository, applicationContext);
  }

  @After
  public void tearDown() throws Exception {
    notificationRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void markNotificationAsSeen(){
    PullRequest pullRequest = new PullRequest();
    pullRequest.assignee = assignee;
    pullRequest.a = assignee;
    notificationService.createClosedPullRequestNotification();
  }
}
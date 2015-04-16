package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.notifications.Notification;
import com.devbliss.gpullr.domain.notifications.NotificationType;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.NotificationRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by abluem on 15/04/15.
 */
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  @Autowired
  public NotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  public List<Notification> allUnseenNotificationsForUser(long receivingUserId) {
    return notificationRepository.findByReceivingUserIdAndSeenIsFalse(receivingUserId);
  }

  public void insertOrUpdate(Notification notification) {
    notificationRepository.save(notification);
  }

  public void markAsSeen(long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotFoundException(String.format("Notification with id=%s not found. :(", notificationId)));
    notification.seen = true;
    notificationRepository.save(notification);
  }

  public void markAllAsSeenForUser(long receivingUserId) {
    List<Notification> notifications = notificationRepository.findByReceivingUserIdAndSeenIsFalse(receivingUserId);
    notifications.forEach(n -> n.seen = true);
    notificationRepository.save(notifications);
  }

  ////
  public void createClosedPullRequestNotification(PullRequest pullRequest) {
    Notification notification = new Notification();
    notification.actor = pullRequest.assignee;
    notification.timestamp = ZonedDateTime.now(ZoneId.systemDefault());
    notification.notificationType = NotificationType.PULLREQUEST_CLOSED;
    notification.pullRequest = pullRequest;
    notification.receivingUserId = pullRequest.author.id;
    notification.seen = false;
    insertOrUpdate(notification);
  }
}

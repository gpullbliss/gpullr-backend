package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.notifications.Notification;
import com.devbliss.gpullr.domain.notifications.NotificationType;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.NotificationRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Notification Service. Notifying the user of any merged / closed pull requests.
 */
@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  private final ZonedDateTime applicationStartupDatetime;

  @Autowired
  public NotificationService(
      NotificationRepository notificationRepository,
      ApplicationContext applicationContext) {
    this.notificationRepository = notificationRepository;
    applicationStartupDatetime = calculateApplicationStartupTime(applicationContext);
  }

  public List<Notification> allUnseenNotificationsForUser(long receivingUserId) {
    return notificationRepository.findByReceivingUserIdAndSeenIsFalse(receivingUserId);
  }

  private void insertOrUpdate(Notification notification) {
    notificationRepository.save(notification);
  }

  public void markAsSeen(long notificationId) {
    Notification notification = notificationRepository
      .findById(notificationId)
      .orElseThrow(() -> new NotFoundException(
          String.format("Notification with id=%s not found.", notificationId)));
    notification.seen = true;
    notificationRepository.save(notification);
  }

  public void markAllAsSeenForUser(long receivingUserId) {
    List<Notification> notifications = notificationRepository.findByReceivingUserIdAndSeenIsFalse(receivingUserId);
    notifications.forEach(n -> n.seen = true);
    notificationRepository.save(notifications);
  }

  public void createClosedPullRequestNotification(PullRequest pullRequest) {

    if (pullRequest.state != State.CLOSED) {
      throw new IllegalArgumentException("Cannot create closedPullRequestNotification for pullrequest " + pullRequest
          + " with state " + pullRequest.state);
    }

    if (isDateAfterApplicationStartup(pullRequest) && closedPullRequestNotificationDoesNotExist(pullRequest)) {
      Notification notification = new Notification();
      notification.actor = pullRequest.assignee;
      notification.timestamp = pullRequest.closedAt;
      notification.notificationType = NotificationType.PULLREQUEST_CLOSED;
      notification.pullRequest = pullRequest;
      notification.receivingUserId = pullRequest.author.id;
      notification.seen = false;
      insertOrUpdate(notification);
    }
  }

  private boolean isDateAfterApplicationStartup(PullRequest pullRequest) {
    boolean before = applicationStartupDatetime.isBefore(pullRequest.closedAt);
    return before;
  }

  private boolean closedPullRequestNotificationDoesNotExist(PullRequest pullRequest) {
    return !notificationRepository.findByPullRequestIdAndTimestamp(pullRequest.id, pullRequest.closedAt).isPresent();
  }

  private ZonedDateTime calculateApplicationStartupTime(ApplicationContext applicationContext) {
    long startupDateTimeEpochMillis = applicationContext.getStartupDate();
    Instant instant = Instant.ofEpochMilli(startupDateTimeEpochMillis);
    ZoneId zone = ZoneId.systemDefault();
    return ZonedDateTime.ofInstant(instant, zone);
  }

}

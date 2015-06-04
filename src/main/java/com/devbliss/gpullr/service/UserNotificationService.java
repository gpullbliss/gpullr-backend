package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Comment;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.notifications.PullRequestClosedUserNotification;
import com.devbliss.gpullr.domain.notifications.PullRequestCommentedUserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotificationType;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.CommentRepository;
import com.devbliss.gpullr.repository.UserNotificationRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Notification Service. Notifying the user of any merged / closed pull requests.
 */
@Service
public class UserNotificationService {

  private final UserNotificationRepository userNotificationRepository;

  private final CommentRepository commentRepository;

  private final ZonedDateTime applicationStartupDatetime;

  @Autowired
  public UserNotificationService(
      UserNotificationRepository userNotificationRepository,
      ApplicationContext applicationContext,
      CommentRepository pullRequestCommentRepository) {
    this.userNotificationRepository = userNotificationRepository;
    this.commentRepository = pullRequestCommentRepository;
    applicationStartupDatetime = calculateApplicationStartupTime(applicationContext);
  }

  public List<UserNotification> allUnseenNotificationsForUser(long receivingUserId) {
    return userNotificationRepository.findByReceivingUserIdAndSeenIsFalse(receivingUserId);
  }

  private void insertOrUpdate(UserNotification notification) {
    userNotificationRepository.save(notification);
  }

  public void markAsSeen(long notificationId) {
    UserNotification notification = userNotificationRepository
      .findById(notificationId)
      .orElseThrow(() -> new NotFoundException(
          String.format("Notification with id=%s not found.", notificationId)));
    notification.seen = true;
    userNotificationRepository.save(notification);
  }

  public void markAllAsSeenForUser(long receivingUserId) {
    List<UserNotification> notifications = userNotificationRepository
      .findByReceivingUserIdAndSeenIsFalse(receivingUserId);
    notifications.forEach(n -> n.seen = true);
    userNotificationRepository.save(notifications);
  }

  public void createClosedPullRequestNotification(PullRequest pullRequest) {
    if (pullRequest.state != State.CLOSED) {
      throw new IllegalArgumentException("Cannot create closedPullRequestNotification for pullrequest " + pullRequest
          + " with state " + pullRequest.state);
    }

    if (isDateAfterApplicationStartup(pullRequest) && closedPullRequestNotificationDoesNotExist(pullRequest)) {
      PullRequestClosedUserNotification notification = new PullRequestClosedUserNotification();
      notification.actor = pullRequest.assignee;
      notification.timestamp = pullRequest.closedAt;
      notification.notificationType = UserNotificationType.PULLREQUEST_CLOSED;
      notification.pullRequest = pullRequest;
      notification.receivingUserId = pullRequest.author.id;
      notification.seen = false;
      insertOrUpdate(notification);
    }
  }

  public void calculateCommentNotifications(PullRequest pullRequest) {
    commentRepository.findAllUnreferenced(pullRequest.id).forEach(this::ensureNotification);
  }

  private void ensureNotification(Comment comment) {

    Optional<UserNotification> existingNotification =
        userNotificationRepository
          .findByPullRequestIdAndNotificationTypeAndSeenIsFalse(
              comment.getPullRequest().id,
              UserNotificationType.PULLREQUEST_COMMENTED);

    PullRequestCommentedUserNotification notification;

    if (existingNotification.isPresent()) {
      notification = (PullRequestCommentedUserNotification) existingNotification.get();
      notification.count++;

    } else {
      notification = new PullRequestCommentedUserNotification();
      notification.receivingUserId = comment.getPullRequest().author.id;
      notification.pullRequest = comment.getPullRequest();
      notification.count = 1;
    }

    userNotificationRepository.save(notification);
    comment.notifications.add(notification);
    commentRepository.save(comment);
  }

  private boolean isDateAfterApplicationStartup(PullRequest pullRequest) {
    boolean before = applicationStartupDatetime.isBefore(pullRequest.closedAt);
    return before;
  }

  private boolean closedPullRequestNotificationDoesNotExist(PullRequest pullRequest) {
    return !userNotificationRepository
      .findByPullRequestIdAndTimestamp(pullRequest.id, pullRequest.closedAt)
      .isPresent();
  }

  private ZonedDateTime calculateApplicationStartupTime(ApplicationContext applicationContext) {
    long startupDateTimeEpochMillis = applicationContext.getStartupDate();
    Instant instant = Instant.ofEpochMilli(startupDateTimeEpochMillis);
    ZoneId zone = ZoneId.systemDefault();
    return ZonedDateTime.ofInstant(instant, zone);
  }

}

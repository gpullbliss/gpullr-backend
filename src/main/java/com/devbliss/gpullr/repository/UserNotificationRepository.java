package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.notifications.UserNotification;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Persists {@link UserNotification} entities.
 * 
 */
public interface UserNotificationRepository extends CrudRepository<UserNotification, Long> {

  List<UserNotification> findAll();

  Optional<UserNotification> findById(Long notificationId);

  List<UserNotification> findByReceivingUserIdAndSeenIsFalse(Long receivingUserId);

  Optional<UserNotification> findByPullRequestIdAndTimestamp(Integer pullRequestId, ZonedDateTime timestamp);
}

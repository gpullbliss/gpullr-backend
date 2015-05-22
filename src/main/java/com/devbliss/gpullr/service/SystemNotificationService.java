package com.devbliss.gpullr.service;

import static com.devbliss.gpullr.domain.notifications.SystemNotificationType.API_RATE_LIMIT_REACHED;

import com.devbliss.gpullr.domain.ApiRateLimitReachedEvent;
import com.devbliss.gpullr.domain.notifications.SystemNotification;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * Notification Service. Notifying the user of any backend system related events.
 * Currently limited to API RATE LIMIT REACHED. All of these messages are held in memory as they are volatile and
 * not required to be persisted.
 *
 * Created by alexander bluem on 13/05/15.
 */
@Service
public class SystemNotificationService implements ApplicationListener<ApiRateLimitReachedEvent> {

  private List<SystemNotification> notifications;

  public SystemNotificationService() {
    notifications = Collections.synchronizedList(new ArrayList<>(10));
  }

  @Override
  public void onApplicationEvent(ApiRateLimitReachedEvent event) {
    SystemNotification notification = new SystemNotification(API_RATE_LIMIT_REACHED, event.resetTime);

    Optional<SystemNotification> notificationOptional = notifications
        .stream()
        .filter(n -> n.notificationType == API_RATE_LIMIT_REACHED)
        .findFirst();

    if (notificationOptional.isPresent()) {
      if (notificationOptional.get().validUntil.isBefore(notification.validUntil)) {
        notificationOptional.get().validUntil = notification.validUntil;
      }
    } else {
      notifications.add(notification);
    }

  }

  /**
   * @return
   */
  public List<SystemNotification> getNotifications() {
    notifications = notifications
        .stream()
        .filter(n -> n.validUntil.isAfter(ZonedDateTime.now()))
        .collect(Collectors.toList());

    return notifications;
  }

}

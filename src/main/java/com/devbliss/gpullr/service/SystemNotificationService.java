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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class SystemNotificationService implements ApplicationListener<ApiRateLimitReachedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemNotificationService.class);

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

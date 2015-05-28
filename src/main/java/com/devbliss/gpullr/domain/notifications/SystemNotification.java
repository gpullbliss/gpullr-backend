package com.devbliss.gpullr.domain.notifications;

import java.time.ZonedDateTime;

/**
 * Entity that holds the timestamp how long to display the notification.
 *
 * Created by alexander bluem on 13/05/15.
 */
public class SystemNotification {
  public ZonedDateTime validUntil;

  public SystemNotificationType notificationType;

  public SystemNotification(SystemNotificationType type, ZonedDateTime resetTime) {
    notificationType = type;
    validUntil = resetTime;
  }
}

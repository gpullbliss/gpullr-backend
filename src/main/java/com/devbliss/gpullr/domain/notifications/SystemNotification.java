package com.devbliss.gpullr.domain.notifications;

import java.time.ZonedDateTime;

/**
 * Created by abluem on 13/05/15.
 */
public class SystemNotification {
  public ZonedDateTime validUntil;

  public SystemNotificationType notificationType;

  public SystemNotification(SystemNotificationType type, ZonedDateTime resetTime) {
    notificationType = type;
    validUntil = resetTime;
  }
}

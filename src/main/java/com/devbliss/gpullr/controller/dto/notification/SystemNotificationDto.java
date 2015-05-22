package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.SystemNotificationType;

/**
 * DTO that holds the timestamp in string representation and the type of notification.
 */
public class SystemNotificationDto {
  public String validUntil;

  public SystemNotificationType notificationType;

}

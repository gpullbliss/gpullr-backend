package com.devbliss.gpullr.controller.dto.notification.usernotification;

import com.devbliss.gpullr.domain.notifications.UserNotificationType;

/**
 * DTO {@link UserNotificationDto} to be transferred by the
 * {@link com.devbliss.gpullr.controller.NotificationController} to the calling client.
 */
public interface UserNotificationDto {

  long getId();

  String getCreatedAt();

  String getType();

}

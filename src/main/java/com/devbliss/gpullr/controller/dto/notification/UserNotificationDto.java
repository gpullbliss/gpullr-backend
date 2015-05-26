package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.UserNotificationType;

/**
 * DTO {@link UserNotificationDto} to be transferred by the
 * {@link com.devbliss.gpullr.controller.NotificationController} to the calling client.
 */
public class UserNotificationDto {

  public long id;

  public String createdAt;

  public UserNotificationType type;

  public String actorName;

  public String repoTitle;

  public String pullRequestTitle;
}

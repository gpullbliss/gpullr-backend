package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.NotificationType;

/**
 * DTO {@link NotificationDto} to be transferred by the {@link com.devbliss.gpullr.controller.NotificationController}
 * to the calling client.
 */
public class NotificationDto {

  public long id;

  public String createdAt;

  public NotificationType type;

  public String actorName;

  public String repoTitle;

  public String pullRequestTitle;
}

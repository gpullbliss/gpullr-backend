package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.NotificationType;

/**
 * Created by abluem on 15/04/15.
 */
public class NotificationDto {

  public long id;

  public String createdAt;

  public NotificationType type;

  public String actorName;

  public String repoTitle;

  public String pullRequestTitle;
}

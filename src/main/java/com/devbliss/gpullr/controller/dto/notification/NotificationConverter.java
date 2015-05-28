package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.SystemNotification;
import com.devbliss.gpullr.domain.notifications.UserNotification;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link UserNotification} entity to DTO {@link UserNotificationDto}
 * to be transferred by the {@link com.devbliss.gpullr.controller.NotificationController} to the calling client. Some
 * information is purposely left out.
 */
@Component
public class NotificationConverter {

  public UserNotificationDto toDto(UserNotification entity) {
    UserNotificationDto dto = new UserNotificationDto();
    dto.createdAt = entity.timestamp.toOffsetDateTime().toString();
    dto.id = entity.id;
    dto.type = entity.notificationType;
    dto.actorName = (entity.actor != null) ? entity.actor.fullName : "";
    dto.pullRequestTitle = entity.pullRequest.title;
    dto.repoTitle = entity.pullRequest.repo.name;
    return dto;
  }

  public SystemNotificationDto toDto(SystemNotification entity) {
    SystemNotificationDto dto = new SystemNotificationDto();
    dto.notificationType = entity.notificationType;
    dto.validUntil = entity.validUntil.toOffsetDateTime().toString();
    return dto;
  }
}

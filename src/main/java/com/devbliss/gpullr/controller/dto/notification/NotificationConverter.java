package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.Notification;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link com.devbliss.gpullr.domain.notifications.Notification} entity to DTO {@link NotificationDto}
 * to be transferred by the {@link com.devbliss.gpullr.controller.NotificationController} to the calling client. Some
 * information is purposely left out.
 */
@Component
public class NotificationConverter {

  public NotificationDto toDto(Notification entity) {
    NotificationDto dto = new NotificationDto();
    dto.createdAt = entity.timestamp.toString();
    dto.id = entity.id;
    dto.type = entity.notificationType;
    dto.actorName = (entity.actor != null) ? entity.actor.fullName : "";
    dto.pullRequestTitle = entity.pullRequest.title;
    dto.repoTitle = entity.pullRequest.repo.name;
    return dto;
  }

}

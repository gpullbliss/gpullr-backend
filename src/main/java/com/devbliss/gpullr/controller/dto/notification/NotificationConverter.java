package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.Notification;
import org.springframework.stereotype.Component;

/**
 * Created by abluem on 15/04/15.
 */
@Component
public class NotificationConverter {

  public NotificationDto toDto(Notification entity) {
    NotificationDto dto = new NotificationDto();
    dto.createdAt = entity.createdAt.toString();
    dto.id = entity.id;
    dto.type = entity.notificationType;
    dto.actorName = (entity.actor != null) ? entity.actor.fullName : "";
    dto.pullRequestTitle = entity.pullRequest.title;
    dto.repoTitle = entity.pullRequest.repo.name;
    return dto;
  }

}

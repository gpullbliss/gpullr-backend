package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.controller.dto.notification.usernotification.MergedNotificationDto;
import com.devbliss.gpullr.controller.dto.notification.usernotification.UserNotificationDto;
import com.devbliss.gpullr.domain.notifications.PullRequestClosedUserNotification;
import com.devbliss.gpullr.domain.notifications.SystemNotification;
import com.devbliss.gpullr.domain.notifications.UserNotification;
import com.devbliss.gpullr.domain.notifications.UserNotificationType;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link UserNotification} entity to DTO {@link UserNotificationDto}
 * to be transferred by the {@link com.devbliss.gpullr.controller.NotificationController} to the calling client. Some
 * information is purposely left out.
 */
@Component
public class NotificationConverter {

  public UserNotificationDto toDto(UserNotification entity) {
    switch (entity.notificationType) {
      case UserNotificationType.PULLREQUEST_CLOSED:
        MergedNotificationDto dto = new MergedNotificationDto();
        dto.setId(entity.id);
        dto.setCreatedAt(entity.timestamp.toOffsetDateTime().toString());
        dto.setActorName((((PullRequestClosedUserNotification) entity).actor != null) ? ((PullRequestClosedUserNotification) entity).actor.fullName : "");
        dto.setPullRequestTitle(entity.pullRequest.title);
        dto.setRepoTitle(entity.pullRequest.repo.name);
        return dto;

      case UserNotificationType.PULLREQUEST_COMMENTED:
        // TODO: implement
        return null;
      default:
        return null;
    }
  }

  public SystemNotificationDto toDto(SystemNotification entity) {
    SystemNotificationDto dto = new SystemNotificationDto();
    dto.notificationType = entity.notificationType;
    dto.validUntil = entity.validUntil.toOffsetDateTime().toString();
    return dto;
  }
}

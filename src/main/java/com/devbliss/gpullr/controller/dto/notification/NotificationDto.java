package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.domain.notifications.SystemNotification;
import java.util.List;

/**
 * Created by abluem on 13/05/15.
 */
public class NotificationDto {
  public List<SystemNotificationDto> systemNotifications;

  public List<UserNotificationDto> userNotifications;
}

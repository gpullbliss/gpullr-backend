package com.devbliss.gpullr.controller.dto.notification;

import com.devbliss.gpullr.controller.dto.notification.usernotification.UserNotificationDto;
import java.util.List;

public class NotificationDto {
  public List<SystemNotificationDto> systemNotifications;

  public List<UserNotificationDto> userNotifications;
}

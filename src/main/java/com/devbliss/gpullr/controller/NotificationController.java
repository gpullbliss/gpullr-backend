package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.notification.NotificationConverter;
import com.devbliss.gpullr.controller.dto.notification.NotificationDto;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.SystemNotificationService;
import com.devbliss.gpullr.service.UserNotificationService;
import com.devbliss.gpullr.service.UserService;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notification Controller delegating data between {@link com.devbliss.gpullr.service.UserNotificationService} and user
 * by REST means.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

  private final UserNotificationService userNotificationService;

  private final SystemNotificationService systemNotificationService;

  private final NotificationConverter notificationConverter;

  private final UserService userService;

  @Autowired
  public NotificationController(UserNotificationService userNotificationService,
      SystemNotificationService systemNotificationService,
      NotificationConverter notificationConverter,
      UserService userService) {
    this.userNotificationService = userNotificationService;
    this.systemNotificationService = systemNotificationService;
    this.notificationConverter = notificationConverter;
    this.userService = userService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public NotificationDto getMyNotifications() {
    User user = userService.whoAmI();
    NotificationDto dto = new NotificationDto();

    dto.userNotifications = userNotificationService
        .allUnseenNotificationsForUser(user.id)
        .stream()
        .map(notificationConverter::toDto)
        .collect(Collectors.toList());

    dto.systemNotifications = systemNotificationService
        .getNotifications()
        .stream()
        .map(notificationConverter::toDto)
        .collect(Collectors.toList());

    return dto;
  }

  @RequestMapping(method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markAllMyNotificationsAsRead() {
    User user = userService.whoAmI();
    userNotificationService.markAllAsSeenForUser(user.id);
  }

  @RequestMapping(value = "/{notificationId}",
      method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markMyNotificationAsRead(@PathVariable("notificationId") @NotNull Long notificationId) {
    userNotificationService.markAsSeen(notificationId);
  }
}

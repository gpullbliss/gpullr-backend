package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ListDto;
import com.devbliss.gpullr.controller.dto.notification.NotificationConverter;
import com.devbliss.gpullr.controller.dto.notification.NotificationDto;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.NotificationService;
import com.devbliss.gpullr.service.UserService;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by abluem on 15/04/15.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {
  private final NotificationService notificationService;
  private final NotificationConverter notificationConverter;
  private final UserService userService;

  @Autowired
  public NotificationController(NotificationService notificationService,
                                NotificationConverter notificationConverter,
                                UserService userService) {
    this.notificationService = notificationService;
    this.notificationConverter = notificationConverter;
    this.userService = userService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ListDto<NotificationDto> getMyNotifications() {
    User user = userService.whoAmI();
    return new ListDto<>(
        notificationService.
            allUnseenNotificationsForUser(user.id).
            stream().
            map(notificationConverter::toDto).
            collect(Collectors.toList()));
  }

  @RequestMapping(method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markAllMyNotificationsAsRead(){
    User user = userService.whoAmI();
    notificationService.allUnseenNotificationsForUser(user.id);
  }

  @RequestMapping(value = "/{notificationId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markMyNotificationAsRead(@RequestParam("notificationId") long notificationId){
    notificationService.markAsSeen(notificationId);
  }
}

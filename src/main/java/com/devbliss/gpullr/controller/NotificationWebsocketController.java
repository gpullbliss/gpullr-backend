package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.notification.NotificationDto;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebsocketController {
  
  @MessageMapping("/hello")
  @SendTo("/notifications/live/queue")
  public NotificationDto bla() {
    NotificationDto notificationDto = new NotificationDto();
    notificationDto.actorName = "Blubberbla";
    return notificationDto;
  }
}

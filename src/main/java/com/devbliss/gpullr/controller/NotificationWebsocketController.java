package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.notification.NotificationDto;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebsocketController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationWebsocketController.class);

  @PostConstruct
  public void init() {
    LOGGER.debug("NotificationWebsocken-Kontrolleur is am Start...");
  }

  
//  @SendTo("/notifications/live/queue")
//  public NotificationDto bla(String message) {
//    LOGGER.debug("WS received: " + message);
//    NotificationDto notificationDto = new NotificationDto();
//    notificationDto.actorName = "Blubberbla: " + message;
//    return notificationDto;
//  }
  
  @MessageMapping("/einfach")
  public void bla(String message) {
    LOGGER.debug("WS received: " + message);
    NotificationDto notificationDto = new NotificationDto();
    notificationDto.actorName = "Blubberbla: " + message;
  }
}

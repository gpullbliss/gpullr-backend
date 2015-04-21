package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.notification.NotificationDto;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class NotificationWebsocketController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationWebsocketController.class);

  @PostConstruct
  public void init() {
    LOGGER.debug("NotificationWebsocken-Kontrolleur is am Start...");
  }

  @MessageMapping("/notify")
  @SendTo("/topic/message")
  public NotificationDto bla(NotificationDto input) {
    LOGGER.debug("WS received: " + input);
    NotificationDto notificationDto = new NotificationDto();
    notificationDto.actorName = "Blubberbla: " + input.repoTitle;
    return notificationDto;
  }
}

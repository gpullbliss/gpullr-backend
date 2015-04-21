package com.devbliss.gpullr.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/notifications").withSockJS();
  }

  // @Override
  // public void configureMessageBroker(MessageBrokerRegistry config) {
  // config.enableSimpleBroker("/notifications/live");
  // config.setApplicationDestinationPrefixes("/app");
  // }
  //
  // @Override
  // public void registerStompEndpoints(StompEndpointRegistry registry) {
  // registry.addEndpoint("/hello").withSockJS();
  // }

}

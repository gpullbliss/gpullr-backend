package com.devbliss.gpullr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point
 */
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
@PropertySource(value = "classpath:secret.properties")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}

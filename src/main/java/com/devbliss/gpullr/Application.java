package com.devbliss.gpullr;

import com.devbliss.gpullr.domain.ApiRateLimitReachedEvent;
import com.devbliss.gpullr.util.Log;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;

/**
 * Application entry point
 */
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class Application {

  @Log
  private static Logger logger;

  public static void main(String[] args) {
    ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);


    TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();

    Runnable runnable = () -> {
      ApiRateLimitReachedEvent event = new ApiRateLimitReachedEvent(run, Instant.now().plus(3, ChronoUnit.MINUTES));
      run.publishEvent(event);
    };

    taskScheduler.scheduleAtFixedRate(runnable, 2 * 60 * 1000);
  }
}

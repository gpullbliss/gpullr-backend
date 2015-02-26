package com.devbliss.gpullr.util;

import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.context.annotation.Configuration;

/**
 * Produces instances of {@link ThreadPoolTaskScheduler} which are already initialized and ready for use.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Configuration
public class ExecutorProducer {
  
  @Bean
  public ThreadPoolTaskScheduler createThreadPoolTaskScheduler() {
    ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
    executor.initialize();
    return executor;
  }
}

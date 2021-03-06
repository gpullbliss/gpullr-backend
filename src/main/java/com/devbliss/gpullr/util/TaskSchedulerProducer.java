package com.devbliss.gpullr.util;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Produces instances of {@link TaskScheduler} which are already initialized and ready for use.
 * Active only for prod and dev profile. For tests, there is {@link TaskSchedulerMockProducer}.
 * 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Configuration
public class TaskSchedulerProducer {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  @Profile({
      "prod", "dev"
  })
  public TaskScheduler createThreadPoolTaskScheduler() {
    ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
    executor.setPoolSize(5);
    executor.initialize();
    return executor;
  }
}

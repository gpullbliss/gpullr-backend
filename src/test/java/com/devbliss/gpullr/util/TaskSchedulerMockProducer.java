package com.devbliss.gpullr.util;

import org.mockito.Answers;

import static org.mockito.Mockito.mock;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Produces Mockito mocks of {@link TaskScheduler} that can be used in verify calls etc..
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Configuration
public class TaskSchedulerMockProducer {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  @Profile("test")
  public TaskScheduler createThreadPoolTaskScheduler() {
    return mock(ThreadPoolTaskScheduler.class, Answers.RETURNS_DEEP_STUBS.get());
  }
}

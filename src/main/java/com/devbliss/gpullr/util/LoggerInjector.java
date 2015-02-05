package com.devbliss.gpullr.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Inject fields that are annotated with {@link Log}.
 * For these fields a Logger is created with the Bean's classname.
 *
 */
@Component
public class LoggerInjector implements BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    ReflectionUtils.doWithFields(bean.getClass(), field -> {
      ReflectionUtils.makeAccessible(field);

      if (field.isAnnotationPresent(Log.class)) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(bean.getClass().getName());
        field.set(bean, logger);
      }
    });
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }
}

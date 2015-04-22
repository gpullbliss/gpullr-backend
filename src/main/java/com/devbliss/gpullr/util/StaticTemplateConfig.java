package com.devbliss.gpullr.util;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Component
//@EnableWebMvc
public class StaticTemplateConfig extends WebMvcConfigurerAdapter {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("socke");
  }
}

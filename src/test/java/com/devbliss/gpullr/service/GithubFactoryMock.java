package com.devbliss.gpullr.service;

import com.devbliss.gpullr.service.github.GithubFactory;

import com.jcabi.github.Github;
import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Creates mock instances of {@link Github}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
@Profile("test")
public class GithubFactoryMock implements GithubFactory {

  @Override
  public Github createClient() {
    try {
      return new MkGithub();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}

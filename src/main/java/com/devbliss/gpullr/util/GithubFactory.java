package com.devbliss.gpullr.util;

import com.jcabi.github.mock.MkGithub;
import java.io.IOException;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class GithubFactory {

  @Value("${github.oauthtoken}")
  private String oauthToken;

  @Bean
  @Profile({"prod", "dev"})
  public Github createClient() {
    return new RtGithub(oauthToken);
  }
  
  @Bean
  @Profile("test")
  public Github createTestClient() {
    try {
      return new MkGithub();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}

package com.devbliss.gpullr.util;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
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
  public Github createJCabiClient() {
    return new RtGithub(oauthToken);
  }

  @Bean
  @Profile("test")
  public Github createJCabiTestClient() {
    try {
      return new MkGithub();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Bean
  @Profile({"prod", "dev"})
  public GithubClient createClient() {
    return new GithubClientImpl();
  }

  @Bean
  @Profile("test")
  public GithubClient createTestClient() {
    return new GithubClientImplNoop();
  }
}

package com.devbliss.gpullr.util;

import org.springframework.context.annotation.Primary;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class GithubFactory {

  @Value("${github.oauthtoken}")
  private String oauthToken;

  @Autowired
  @Qualifier("githubClientImpl")
  private GithubClientImpl githClientImpl;

  @Bean
  @Profile({
      "prod", "dev"
  })
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
  @Profile({
      "prod", "dev"
  })
  @Primary
  public GithubClient createClient() {
    return githClientImpl;
  }

  @Bean
  @Profile("test")
  @Primary
  public GithubClient createTestClient() {
    return new GithubClientImplNoop();
  }
}

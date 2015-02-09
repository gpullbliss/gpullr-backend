package com.devbliss.gpullr.service.github;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Factory implementation that creates "real" instances of {@link Github}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
@Profile({"prod", "dev"})
public class GithubFactoryImpl implements GithubFactory {
  
  @Value("${github.oauthtoken}")
  private String oauthToken;

  @Override
  public Github createClient() {
    return new RtGithub(oauthToken);
  }
}

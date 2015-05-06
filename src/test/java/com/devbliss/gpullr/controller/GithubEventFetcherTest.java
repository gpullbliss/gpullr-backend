package com.devbliss.gpullr.controller;

import org.junit.Before;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.RepoCreatedEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class GithubEventFetcherTest {

  @Autowired
  private GithubEventFetcher githubEventFetcher;
  
  @Autowired
  private ApplicationContext applicationContext;
  
  private Repo repo;
  
  @Before
  public void setup() {
    repo = new Repo();
    repo.name = "sometestrepo";
  }
  
  @Test
  public void onApplicationEvent() {
    applicationContext.publishEvent(new RepoCreatedEvent(this, repo));
  }
}

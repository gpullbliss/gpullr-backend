package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.service.CacheAgeService;
import com.devbliss.gpullr.service.GithubRepoService;
import com.devbliss.gpullr.service.GithubService;
import com.devbliss.gpullr.util.Log;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Triggers check whether our data needs refreshment from GitHub and also triggers the refresh if required.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class InitialDataRefresher {

  @Log
  private Logger logger;
  
  @Autowired
  private CacheAgeService cacheAgeService;

  @Autowired
  private GithubService githubService;

  @Autowired
  private GithubRepoService githubRepoService;

  public InitialDataRefresher() {

  }
  
  @PostConstruct
  public void refreshIfRequired() {
    logger.info("Checking data age...");
    
    if (cacheAgeService.isRefreshRequired()) {
      logger.info("Data need refreshment...");
      refreshGithubRepos();
      cacheAgeService.setRefreshedToday();
      logger.info("Data refreshed.");
    }
    
    logger.info("Finished checking data age.");
  }

  private void refreshGithubRepos() {
    githubService.fetchAllGithubRepos().forEach(r -> githubRepoService.insertOrUpdate(r));
  }
}

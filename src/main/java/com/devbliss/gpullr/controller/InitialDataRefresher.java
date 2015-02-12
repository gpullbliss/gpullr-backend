package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.service.github.GithubApi;

import com.devbliss.gpullr.service.CacheAgeService;
import com.devbliss.gpullr.service.RepoService;
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
  private GithubApi githubApi;

  @Autowired
  private RepoService repoService;

  public InitialDataRefresher() {

  }
  
  @PostConstruct
  public void refreshIfRequired() {
    logger.info("Checking data age...");
    
    if (cacheAgeService.isRefreshRequired()) {
      logger.info("Data need refreshment...");
      cacheAgeService.setRefreshedToday();
      logger.info("Data refreshed.");
    } else {
      logger.info("Everything up to date!");
    }
    
    logger.info("Finished checking data age.");
  }
}

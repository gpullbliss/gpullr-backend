package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.service.github.GithubApi;

import org.springframework.stereotype.Component;
import com.devbliss.gpullr.service.GithubRepoService;
import com.devbliss.gpullr.util.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Since the events endpoint of GitHub does *not* deliver the event that a new repository has been created
 * (unlike the documentation tells), we periodically retrieve the full list of repositories and store
 * changes in our persistence layer.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class GithubReposRefreshScheduler {
  
//  private static final long DELAY_IN_MILLIS = 1000 * 60 * 60; // every hour
  private static final long DELAY_IN_MILLIS = 1000 * 60 * 5; // every 5 minutes
  
  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubService;

  @Autowired
  private GithubRepoService githubRepoService;
  
  @Scheduled(fixedDelay=DELAY_IN_MILLIS)
  public void refreshGithubRepos() {
    logger.info("Refreshing github repos...");
    githubService.fetchAllGithubRepos().forEach(r -> githubRepoService.insertOrUpdate(r));
    logger.info("Github repos refreshed.");
  }
}

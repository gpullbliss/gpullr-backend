package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.util.Log;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by abluem on 05/06/15.
 */
public class GithubPullRequestFetcher extends AbstractFixedScheduleWorker {

  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private PullRequestService pullRequestService;

  @Override
  protected void execute() {

//    List<PullRequest> fetchedPullRequests = githubApi.fetchAllPullRequests(listOfRepos, etagHeader);

//    fetchedPullRequests.forEach(r -> {
//      logger.debug(String.format("fetched pull request: %s", r.toString()));
//      pullRequestService.insertOrUpdate(r);
//    });
  }

  /**
   * RUN ONLY ONCE AFTER fetching repos
   */
  @Override
  protected Date nextExecution() {
    return Date.from(Instant.now().plus(2, ChronoUnit.MINUTES));
  }
}

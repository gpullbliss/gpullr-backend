package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.util.Log;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Since the events endpoint of GitHub does *not* deliver the event that a new repository has been created
 * (unlike the documentation tells), this class fetches the full list of repositories and stores
 * changes in our persistence layer.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class GithubReposFetcher extends AbstractFixedScheduleWorker {

  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private RepoService repoService;

  @Override
  protected void execute() {
    List<Repo> fetchedRepos = githubApi.fetchAllGithubRepos();

    fetchedRepos.forEach(r -> {
      logger.debug(String.format("fetched repo: %d %s", r.id, r.name));
    });

    repoService.setActiveRepos(fetchedRepos);
  }

  /**
   * 
   */
  @Override
  protected Date nextExecution() {
    return Date.from(Instant.now().plus(30, ChronoUnit.MINUTES));
  }
}

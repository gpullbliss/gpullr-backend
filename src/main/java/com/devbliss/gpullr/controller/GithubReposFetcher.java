package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.util.Log;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Since the events endpoint of GitHub does *not* deliver the event that a new repository has been created
 * (unlike the documentation tells), this class fetches the full list of repositories and store
 * changes in our persistence layer.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class GithubReposFetcher {

  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private RepoService repoService;

  public void fetchRepos() {
    logger.info("Fetching repos from GitHub...");

    List<Repo> repos = githubApi.fetchAllGithubRepos();

    repos.forEach(r -> {
          logger.info(String.format("fetched repo: [%d][%s]", r.id, r.name));
          repoService.insertOrUpdate(r);
        }
    );

    logger.info("Finished fetching repos from GitHub.");
  }
}

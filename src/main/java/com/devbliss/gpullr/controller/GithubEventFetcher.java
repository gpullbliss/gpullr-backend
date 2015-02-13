package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.PullrequestService;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.service.github.GithubEventsResponse;
import com.devbliss.gpullr.util.Log;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Periodically fetches 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class GithubEventFetcher {

  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private RepoService repoService;

  @Autowired
  private PullrequestService pullrequestService;

  public void fetchEvents() {

    logger.info("Start fetching events from GitHub...");

    for (Repo repo : repoService.findAll()) {
      logger.debug("Fetch events for repo: " + repo.name);
      fetchEvents(repo);
    }

    logger.info("Finished fetching events from GitHub.");
  }

  private void fetchEvents(Repo repo) {
    GithubEventsResponse response = githubApi.fetchAllEvents(repo, Optional.empty());
    response.pullrequestEvents.forEach(ev -> pullrequestService.insertOrUpdate(ev.pullrequest));
  }
}

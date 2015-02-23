package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.PullrequestEventHandler;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.PullrequestService;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.service.github.GithubEventsResponse;
import com.devbliss.gpullr.util.Log;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Fetches pull requests for all our repositories. Must be started once using {@link #startFetchEventsLoop()}.
 * Afterwards, it independently polls periodically again according to the poll interval returned by GitHub.
 * 
 * The actual bussiness logic for handling the fetched events takes place in {@link PullrequestEventHandler}.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
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

  @Autowired
  private PullrequestEventHandler pullrequestEventHandler;

  private ThreadPoolTaskScheduler executor;

  public GithubEventFetcher() {
    executor = new ThreadPoolTaskScheduler();
    executor.initialize();
  }

  public void startFetchEventsLoop() {

    logger.info("Start fetching events from GitHub...");

    for (Repo repo : repoService.findAll()) {
      logger.debug("Fetch events for repo: " + repo.name);
      fetchEvents(repo, Optional.empty());
    }

    logger.info("Finished fetching events from GitHub.");
  }

  private void fetchEvents(Repo repo, Optional<String> etagHeader) {
    handleEventsResponse(githubApi.fetchAllEvents(repo, etagHeader), repo);
  }
  
  private void handleEventsResponse(GithubEventsResponse response, Repo repo) {
    response.pullrequestEvents.forEach(pullrequestEventHandler::handlePullrequestEvent);
    logger.debug("Fetched " + response.pullrequestEvents.size() + " PR events for " + repo.name);
    Date start = Date.from(Instant.now().plusSeconds(response.nextRequestAfterSeconds));
    executor.schedule(() -> fetchEvents(repo, response.etagHeader), start);
  }
}

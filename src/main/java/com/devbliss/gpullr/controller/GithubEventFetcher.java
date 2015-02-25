package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.service.github.GithubEventsResponse;
import com.devbliss.gpullr.service.github.PullRequestEventHandler;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Fetches pull requests for all our repositories. Must be started once using {@link #startFetchEventsLoop()}.
 * Afterwards, it independently polls periodically according to the poll interval returned by GitHub.
 * 
 * The actual business logic for handling the fetched events takes place in {@link PullRequestEventHandler}.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class GithubEventFetcher {

  private static final Logger logger = LoggerFactory.getLogger(GithubEventFetcher.class);

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private RepoService repoService;

  @Autowired
  private PullRequestEventHandler pullRequestEventHandler;

  private ThreadPoolTaskScheduler executor;

  public GithubEventFetcher() {
    executor = new ThreadPoolTaskScheduler();
    executor.initialize();
  }

  public void startFetchEventsLoop() {

    List<String> tmpBlacklist = Arrays.asList("gwtbliss", "risotto", "idpool-domainmodels", "devblissdemoproject",
        "juanitor");

    logger.info("Start fetching events from GitHub...");
    int i = 0;

    for (Repo repo : repoService.findAll()) {
      // if(!tmpBlacklist.contains(repo.name)) {
      // if (repo.name.equals("epubli-autorencockpit") || repo.name.equals("gwtbliss") || i < 5) {
      // if(repo.name.equals("coporate_design")) {
      System.err.println("Fetched events for " + i + ". repo: " + repo.name);
      fetchEvents(repo, Optional.empty());
      i++;

      if (i == 5) {
        try {
          Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        i = 0;
      }

      // }

    }

    logger.info("Finished fetching events from GitHub.");
  }

  private void fetchEvents(Repo repo, Optional<String> etagHeader) {
    handleEventsResponse(githubApi.fetchAllEvents(repo, etagHeader), repo);
  }

  private void handleEventsResponse(GithubEventsResponse response, Repo repo) {
    response.pullRequestEvents.forEach(pullRequestEventHandler::handlePullRequestEvent);
    logger.debug("Fetched " + response.pullRequestEvents.size() + " PR events for " + repo.name);
    Date start = Date.from(Instant.now().plusSeconds(response.nextRequestAfterSeconds));
    executor.schedule(() -> fetchEvents(repo, response.etagHeader), start);
  }
}

package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Event;
import com.devbliss.gpullr.domain.PullRequestCommentEvent;
import com.devbliss.gpullr.domain.PullRequestEvent;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.RepoCreatedEvent;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.CommentEventHandler;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.service.github.GithubEventsResponse;
import com.devbliss.gpullr.service.github.PullRequestEventHandler;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
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
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GithubEventFetcher implements ApplicationListener<RepoCreatedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GithubEventFetcher.class);

  private final GithubApi githubApi;

  private final RepoService repoService;

  private final PullRequestEventHandler pullRequestEventHandler;

  private final ThreadPoolTaskScheduler executor;
  
  private final CommentEventHandler commentEventHandler;

  @Autowired
  public GithubEventFetcher(
      GithubApi githubApi,
      RepoService repoService,
      PullRequestEventHandler pullRequestEventHandler,
      ThreadPoolTaskScheduler executor, 
      CommentEventHandler commentEventHandler) {
    this.githubApi = githubApi;
    this.repoService = repoService;
    this.pullRequestEventHandler = pullRequestEventHandler;
    this.executor = executor;
    this.commentEventHandler = commentEventHandler;
  }

  /**
   * Starts the fetching loop. Must be called once at application start.
   */
  public void startFetchEventsLoop() {
    List<Repo> allActiveRepos = repoService.findAllActive();
    LOGGER.info("Start fetching events from GitHub for all " + allActiveRepos.size() + " repos...");
    int counter = 1;

    for (Repo repo : allActiveRepos) {
      LOGGER.debug("Fetching events for repo (initial loop): " + repo.name + " (" + counter + ". in list)");
      fetchEvents(repo, Optional.empty());
      counter++;
    }
  }

  @Override
  public void onApplicationEvent(RepoCreatedEvent event) {
    LOGGER.debug("Added new repo to fetch events loop: " + event.createdRepo.name);
    fetchEvents(event.createdRepo, Optional.empty());
  }

  private void fetchEvents(Repo repo, Optional<String> etagHeader) {
    handleEventsResponse(githubApi.fetchAllEvents(repo, etagHeader), repo);
  }

  /**
   * Fetches events again. Meant to be called by scheduler. Refreshes the repo data from database 
   * in case the name has changed which influences the URI for the call.
   * 
   * @param repo
   * @param etagHeader
   */
  private void fetchEventsAgain(Repo repo, Optional<String> etagHeader) {
    repoService.findById(repo.id).ifPresent(r -> {
      LOGGER.debug("Fetching events for repo (scheduled): " + r.name);
      fetchEvents(r, etagHeader);
    });
  }

  private void handleEventsResponse(GithubEventsResponse response, Repo repo) {
    response.payload.forEach(event -> handleEvent(event));
    Date start = Date.from(response.nextFetch);
    executor.schedule(() -> fetchEventsAgain(repo, response.etagHeader), start);
    LOGGER.debug("Fetched "
        + response.payload.size()
        + " PR events for " + repo.name
        + " / next fetch=" + start
        + " / active threads in executor="
        + executor.getActiveCount() + ", queueSize="
        + executor.getScheduledThreadPoolExecutor().getQueue().size());
  }

  private void handleEvent(Event event) {
    if(event.getClass().equals(PullRequestEvent.class)) {
      pullRequestEventHandler.handlePullRequestEvent((PullRequestEvent)event);
    }

    else if(event.getClass().equals(PullRequestCommentEvent.class)){
      commentEventHandler.handleCommentEvent((PullRequestCommentEvent) event);
    }
  }
}

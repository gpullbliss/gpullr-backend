package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.service.github.GithubEventsResponse;
import java.util.Optional;
import javax.annotation.PostConstruct;
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

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private RepoService repoService;

  @PostConstruct
  public void fetchEvents() {

    System.err.println("### *********** fetch events");

    for (Repo repo : repoService.findAll()) {
      System.err.println("### *********** fetch events for repo: " + repo.name);
      fetchEvents(repo);
    }
  }

  private void fetchEvents(Repo repo) {
    GithubEventsResponse response = githubApi.fetchAllEvents(repo, Optional.empty());
  }
}

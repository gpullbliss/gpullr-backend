package com.devbliss.gpullr.controller;

import java.util.Optional;

import com.devbliss.gpullr.service.github.GithubEventsResponse;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
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
    for(Repo repo: repoService.findAll()) {
      fetchEvents(repo);
    }
  }
  
  private void fetchEvents(Repo repo) {
    GithubEventsResponse response = githubApi.fetchAllEvents(repo, Optional.empty());
  }
}

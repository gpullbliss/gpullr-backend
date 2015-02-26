package com.devbliss.gpullr.service;

import com.devbliss.gpullr.controller.GithubEventFetcher;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.repository.RepoRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business facade for persisting and retrieving {@link Repo} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class RepoService {

  private final RepoRepository repoRepository;

  private final GithubEventFetcher githubEventFetcher;

  @Autowired
  public RepoService(RepoRepository repoRepository, GithubEventFetcher githubEventFetcher) {
    this.repoRepository = repoRepository;
    this.githubEventFetcher = githubEventFetcher;
  }

  public Optional<Repo> findByName(String name) {
    return repoRepository.findByName(name);
  }

  public void insertOrUpdate(Repo repo) {
    Optional<Repo> existing = repoRepository.findById(repo.id);
    repoRepository.save(repo);

    if (!existing.isPresent()) {
      githubEventFetcher.addRepoToFetchEventsLooop(repo);
    }
  }

  public List<Repo> findAll() {
    return StreamSupport.stream(repoRepository.findAll().spliterator(), false).collect(Collectors.toList());
  }
}

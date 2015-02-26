package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.RepoCreatedEvent;
import com.devbliss.gpullr.repository.RepoRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Business facade for persisting and retrieving {@link Repo} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class RepoService {

  private final RepoRepository repoRepository;

  private final ApplicationContext applicationContext;

  @Autowired
  public RepoService(RepoRepository repoRepository, ApplicationContext applicationContext) {
    this.repoRepository = repoRepository;
    this.applicationContext = applicationContext;
  }

  public Optional<Repo> findByName(String name) {
    return repoRepository.findByName(name);
  }

  public void insertOrUpdate(Repo repo) {
    Optional<Repo> existing = repoRepository.findById(repo.id);
    repoRepository.save(repo);

    if (!existing.isPresent()) {
      applicationContext.publishEvent(new RepoCreatedEvent(this, repo));
    }
  }

  public List<Repo> findAll() {
    return StreamSupport.stream(repoRepository.findAll().spliterator(), false).collect(Collectors.toList());
  }
}

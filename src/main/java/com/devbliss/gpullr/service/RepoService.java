package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.RepoCreatedEvent;
import com.devbliss.gpullr.repository.RepoRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(RepoService.class);

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

  public List<Repo> findAll() {
    return repoRepository.findAll();
  }

  /**
   * Sets the list of active repos. All repos in the given list will be in the database afterwards. The ones
   * already stored in the database will be updated in case they have changed. The ones in the database which are
   * NOT in the given list will be inactive afterwards.
   *
   * @param repos
   */
  public void setActiveRepos(List<Repo> repos) {
    List<Repo> existingRepos = repoRepository.findAll();
    existingRepos.stream().filter(r -> !repos.contains(r)).forEach(r -> {
      LOGGER.info("Deactivating local repo '{}'", r.name);
      r.active = false;
      repoRepository.save(r);
    });

    repos.stream().filter(r -> !existingRepos.contains(r)).forEach(r -> {
      LOGGER.info("Firing repo created event for new repo '{}'", r.name);
      applicationContext.publishEvent(new RepoCreatedEvent(this, r));
    });
    repoRepository.save(repos);
  }

  public List<Repo> findAllActive() {
    return repoRepository.findAllByActive(true);
  }
}

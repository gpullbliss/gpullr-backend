package com.devbliss.gpullr.service;

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

  @Autowired
  public RepoService(RepoRepository repoRepository) {
    this.repoRepository = repoRepository;
  }

  public Optional<Repo> findByName(String name) {
    return repoRepository.findByName(name);
  }

  public void insertOrUpdate(Repo repo) {
    repoRepository.save(repo);
  }

  public List<Repo> findAll() {
    return StreamSupport.stream(repoRepository.findAll().spliterator(), false).collect(Collectors.toList());
  }
}

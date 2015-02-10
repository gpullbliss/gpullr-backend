package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.GithubRepo;
import com.devbliss.gpullr.repository.GithubRepoRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business facade for persisting and retrieving {@link GithubRepo} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class GithubRepoService {
  
  private final GithubRepoRepository githubRepoRepository;
  
  @Autowired
  public GithubRepoService(GithubRepoRepository githubRepoRepository) {
    this.githubRepoRepository = githubRepoRepository;
  }
  
  public Optional<GithubRepo> findByName(String name) {
    return githubRepoRepository.findByName(name);
  }
  
  public void insertOrUpdate(GithubRepo githubRepo) {
    githubRepoRepository.save(githubRepo);
  }
  
  public List<GithubRepo> findAll() {
    return StreamSupport.stream(githubRepoRepository.findAll().spliterator(), false).collect(Collectors.toList());
  }
}

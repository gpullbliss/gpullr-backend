package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.GithubRepo;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Persists {@link GithubRepo} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface GithubRepoRepository extends CrudRepository<GithubRepo, Integer>{
  
  Optional<GithubRepo> findByName(String name);
  
}

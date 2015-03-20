package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.Repo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Persists {@link Repo} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public interface RepoRepository extends CrudRepository<Repo, Integer> {

  Optional<Repo> findByName(String name);

  Optional<Repo> findById(Integer id);

  List<Repo> findAll();
}

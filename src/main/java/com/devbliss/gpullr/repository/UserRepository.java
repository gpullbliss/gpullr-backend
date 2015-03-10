package com.devbliss.gpullr.repository;

import java.util.Optional;

import com.devbliss.gpullr.domain.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * Persistence layer for {@link com.devbliss.gpullr.domain.User} objects.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

  List<User> findAll();

  List<User> findByCanLoginIsTrue();
  
  Optional<User> findById(Integer id);
}

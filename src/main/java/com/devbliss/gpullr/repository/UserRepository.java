package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Persistence layer for {@link com.devbliss.gpullr.domain.User} objects.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

  List<User> findAll();

  List<User> findByCanLoginIsTrue();
  
  Optional<User> findById(Integer id);
}

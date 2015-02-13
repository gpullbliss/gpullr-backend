package com.devbliss.gpullr.repository;

import java.util.List;

import com.devbliss.gpullr.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Persistence layer for {@link com.devbliss.gpullr.domain.User} objects.
 */
@Component
public interface UserRepository extends CrudRepository<User, Long> {

  List<User> findAll();

}

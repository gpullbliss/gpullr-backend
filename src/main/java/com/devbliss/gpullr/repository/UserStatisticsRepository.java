package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserStatistics;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Stores {@link UserStatistics} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface UserStatisticsRepository extends CrudRepository<UserStatistics, Long> {

  List<UserStatistics> findAll();

  Optional<UserStatistics> findByUserId(Integer userId);
}

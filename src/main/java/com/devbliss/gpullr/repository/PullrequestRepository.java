package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.Pullrequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Handles persistence of {@link Pullrequest} entities.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public interface PullrequestRepository extends CrudRepository<Pullrequest, Integer> {

  List<Pullrequest> findAll();

  List<Pullrequest> findAllByState(Pullrequest.State state);

  Optional<Pullrequest> findById(Integer id);
}

package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Handles persistence of {@link PullRequest} entities.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public interface PullRequestRepository extends CrudRepository<PullRequest, Integer> {

  List<PullRequest> findAll();

  List<PullRequest> findAllByState(PullRequest.State state);
  

  Optional<PullRequest> findById(Integer id);

  List<PullRequest> findByAssigneeAndState(User assignee, State state);
}

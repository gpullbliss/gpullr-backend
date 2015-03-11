package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.ClosedPullRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Stores {@link ClosedPullRequest} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface UserHasClosedPullRequestRepository extends CrudRepository<ClosedPullRequest, Long> {

  List<ClosedPullRequest> findByUser(User user);

  Optional<ClosedPullRequest> findByPullRequestUrl(String pullRequestUrl);
}

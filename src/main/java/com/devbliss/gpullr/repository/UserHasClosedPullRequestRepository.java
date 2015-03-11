package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserHasClosedPullRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Stores {@link UserHasClosedPullRequest} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface UserHasClosedPullRequestRepository extends CrudRepository<UserHasClosedPullRequest, Long> {

  List<UserHasClosedPullRequest> findByUser(User user);

  Optional<UserHasClosedPullRequest> findByPullRequestUrl(String pullRequestUrl);
}

package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.repository.PullrequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business facade for persisting and retrieving {@link Pullrequest} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */

@Service
public class PullrequestService {

  private final PullrequestRepository pullrequestRepository;

  private final UserRepository userRepository;

  @Autowired
  public PullrequestService(PullrequestRepository pullrequestRepository, UserRepository userRepository) {
    this.pullrequestRepository = pullrequestRepository;
    this.userRepository = userRepository;
  }

  public List<Pullrequest> findAll() {
    return pullrequestRepository
      .findAll()
      .stream()
      .sorted((p1, p2) -> p1.createdAt.compareTo(p2.createdAt))
      .collect(Collectors.toList());
  }

  public void insertOrUpdate(Pullrequest pullrequest) {
    if (userRepository.findOne(pullrequest.owner.id) == null) {
      userRepository.save(pullrequest.owner);
    }
    pullrequestRepository.save(pullrequest);
  }
}

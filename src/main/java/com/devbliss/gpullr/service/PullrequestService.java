package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.PullrequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.service.github.GithubApi;
import java.util.List;
import java.util.Optional;
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

  private final GithubApi githubApi;

  @Autowired
  public PullrequestService(
      PullrequestRepository pullrequestRepository,
      UserRepository userRepository,
      GithubApi githubApi) {
    this.pullrequestRepository = pullrequestRepository;
    this.userRepository = userRepository;
    this.githubApi = githubApi;
  }

  public List<Pullrequest> findAll() {

    return pullrequestRepository
      .findAll()
      .stream()
      .sorted((p1, p2) -> p1.createdAt.compareTo(p2.createdAt))
      .collect(Collectors.toList());
  }

  public List<Pullrequest> findAllOpen() {
    return pullrequestRepository
      .findAllByState(Pullrequest.State.OPEN)
      .stream()
      .sorted((p1, p2) -> p1.createdAt.compareTo(p2.createdAt))
      .collect(Collectors.toList());
  }

  public Optional<Pullrequest> findById(Integer id) {
    return pullrequestRepository.findById(id);
  }

  public boolean exists(Integer id) {
    return pullrequestRepository.exists(id);
  }

  public void assignPullrequest(User user, Integer pullrequestId) {
    Pullrequest pullrequest = pullrequestRepository
      .findById(pullrequestId)
      .orElseThrow(() -> new NotFoundException("No pullrequest found with id " + pullrequestId));

    if (!doesUserExist(user)) {
      throw new NotFoundException("Cannot assign unknown user " + user.username + " to a pullrequest.");
    }

    githubApi.assingUserToPullRequest(user, pullrequest);
    pullrequest.assignee = user;
    pullrequestRepository.save(pullrequest);
  }

  public void insertOrUpdate(Pullrequest pullrequest) {
    if (!doesUserExist(pullrequest.owner)) {
      userRepository.save(pullrequest.owner);
    }
    pullrequestRepository.save(pullrequest);
  }

  private boolean doesUserExist(User user) {
    return userRepository.findOne(user.id) != null;
  }
}

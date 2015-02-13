package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.repository.PullrequestRepository;
import com.devbliss.gpullr.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // TODO sort
    return pullrequestRepository.findAll();
  }

  public void assignPullrequest(String sessionId, Integer pullrequestId) {
    Pullrequest pullrequest = pullrequestRepository.findOne(pullrequestId);
  }

  public void insertOrUpdate(Pullrequest pullrequest) {
    userRepository.save(pullrequest.owner);
    pullrequestRepository.save(pullrequest);
  }
}

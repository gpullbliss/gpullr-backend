package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.repository.PullrequestRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class PullrequestService {

  private final PullrequestRepository pullrequestRepository;

  @Autowired
  public PullrequestService(PullrequestRepository pullrequestRepository) {
    this.pullrequestRepository = pullrequestRepository;
  }

  public List<Pullrequest> findAll() {
    // TODO sort
    return pullrequestRepository.findAll();
  }

  public void assignPullrequest(String sessionId, Integer pullrequestId) {

  }
}

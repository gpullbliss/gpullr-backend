package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.PullrequestRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class PullrequestService {

  private final PullrequestRepository pullrequestRepository;

  @Autowired
  public PullrequestService(PullrequestRepository pullrequestRepository) {
    this.pullrequestRepository = pullrequestRepository;
  }

  public List<Pullrequest> findAll() {

    Pullrequest pr1 = new Pullrequest();
    pr1.id = 12345;
    pr1.url = "https://github.com/devbliss/manuals/pull/44";
    Repo r1 = new Repo();
    r1.description = "repo description";
    r1.id = 123;
    r1.name = "manuals";
    pr1.repo = r1;
    User user = new User();
    user.fullname = "Ömer Karahan";
    user.avatarUrl = "https://avatars2.githubusercontent.com/u/3127128?v=3";
    pr1.author = user;
    pr1.createdAt = ZonedDateTime.now();
    pr1.changedFiles = 1;
    pr1.additions = 112;
    pr1.deletions = 0;
    pr1.state = Pullrequest.State.MERGED;


    Pullrequest pr2 = new Pullrequest();

    // TODO sort
    return pullrequestRepository.findAll();
  }

  public void assignPullrequest(String sessionId, Integer pullrequestId) {
    Pullrequest pullrequest = pullrequestRepository.findOne(pullrequestId);
  }
}

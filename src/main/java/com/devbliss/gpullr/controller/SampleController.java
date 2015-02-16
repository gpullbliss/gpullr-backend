package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.PullrequestService;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.github.GithubApi;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

  @Autowired
  private RepoService repoService;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private PullrequestService pullrequestService;

  @RequestMapping(value = "/repos", produces = "application/json", method = RequestMethod.GET)
  public List<Repo> getAllRepos() {
    return repoService.findAll();
  }

  @RequestMapping(value = "test")
  public String  test() throws Exception {
    User user = new User(123, "username", "avatar_url");

    Repo repo = new Repo();
    repo.name = "repo_name";

    Pullrequest pr = new Pullrequest();
    pr.id = 5;
    pr.title = "PR title";
    pr.repo = repo;

    githubApi.assingUserToPullRequest(user, pr);

    return "ok";
  }

  @RequestMapping("/testpulls")
  public List<Pullrequest> getAllPullRequests() {
    return pullrequestService.findAll();
  }
}

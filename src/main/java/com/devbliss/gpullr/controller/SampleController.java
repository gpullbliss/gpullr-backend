package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.RepoService;
import com.devbliss.gpullr.service.UserService;
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
  private PullRequestService pullRequestService;

  @Autowired
  private UserService userService;

  @RequestMapping(value = "/repos", produces = "application/json", method = RequestMethod.GET)
  public List<Repo> getAllRepos() {
    return repoService.findAll();
  }

  @RequestMapping(value = "test")
  public String test() throws Exception {
    User user = null;
    List<User> users = userService.findAll();
    for (int i = 0; i < users.size(); i++) {
      if (users.get(i).username.equals("dwalldorf")) {
        user = users.get(i);
      }
    }

    PullRequest pr = null;
    List<PullRequest> prs = pullRequestService.findAll();

    for (int i = 0; i < prs.size(); i++) {
      String title = prs.get(i).title;
      if (title != null && title.contains("blubb")) {
        pr = prs.get(i);
      }
    }

    githubApi.assingUserToPullRequest(user, pr);

    return "ok";
  }

  @RequestMapping("/testpulls")
  public List<PullRequest> getAllPullRequests() {
    return pullRequestService.findAll();
  }
}

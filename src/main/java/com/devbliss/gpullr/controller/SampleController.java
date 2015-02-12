package com.devbliss.gpullr.controller;


import com.devbliss.gpullr.service.github.GithubApi;

import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.service.RepoService;
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
  
  @RequestMapping(value="/repos", produces="application/json", method = RequestMethod.GET)
  public List<Repo> getAllRepos() {
    return repoService.findAll();
  }
}

package com.devbliss.gpullr.controller;


import com.devbliss.gpullr.service.github.GithubApi;

import com.devbliss.gpullr.domain.GithubRepo;
import com.devbliss.gpullr.service.GithubRepoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
  
  @Autowired
  private GithubRepoService githubRepoService;
  
  @Autowired
  private GithubApi githubService;
  
  @RequestMapping(value="/repos", produces="application/json", method = RequestMethod.GET)
  public List<GithubRepo> getAllRepos() {
    return githubRepoService.findAll();
  }
}

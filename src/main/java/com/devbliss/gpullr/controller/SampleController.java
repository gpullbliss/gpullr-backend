package com.devbliss.gpullr.controller;


import com.devbliss.gpullr.service.github.GithubService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleController {
  
  @Autowired
  private GithubService githubService;
  
  @RequestMapping(method=RequestMethod.GET)
  public List<String> getShasOfDementity() {
    try {
      return githubService.proofThatItWorks();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}

package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.Pullrequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage pull requests.
 */
@RestController
@RequestMapping("/pulls")
public class PullsController {

  @RequestMapping(method = RequestMethod.GET)
  public List<Pullrequest> getPullrequests() {
    List<Pullrequest> pullrequests = new ArrayList<Pullrequest>();
    return pullrequests;
  }

  @RequestMapping(method = RequestMethod.POST)
  public void assignPullrequest(@RequestBody String GitHubOAuthToken) {

  }

}

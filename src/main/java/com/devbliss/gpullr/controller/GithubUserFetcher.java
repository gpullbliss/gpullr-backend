package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.service.github.GithubApi;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class GithubUserFetcher {

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private UserService userService;

  @PostConstruct
  public void fetchUsers() throws IOException {
    List<User> users = githubApi.fetchAllOrgaMembers();
    users.forEach(user-> System.out.println(user.username));
    users.forEach(userService::save);
  }

}

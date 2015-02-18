package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.util.Log;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Fetches all users belonging to Devbliss from GitHub API.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class GithubUserFetcher {

  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private UserService userService;

  public void fetchUsers() {
    try {
      logger.info("Start fetching users from GitHub...");
      List<User> users = githubApi.fetchAllOrgaMembers();
      users.forEach(u -> handleUser(u));
      logger.info("Finished fetching users from GitHub,");
    } catch (IOException e) {
      logger.error("Error fetching users from GitHub: " + e.getMessage(), e);
    }
  }

  private void handleUser(User user) {
    logger.debug("fetched user: " + user.username);
    user.canLogin = true;
    userService.insertOrUpdate(user);
  }

}

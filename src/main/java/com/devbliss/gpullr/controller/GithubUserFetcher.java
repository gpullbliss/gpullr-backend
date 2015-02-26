package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.util.Log;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Fetches all users belonging to Devbliss from GitHub API.
 */
@Component
public class GithubUserFetcher extends AbstractFixedScheduleFetcher {

  private static final int HOURS_OF_DAY = 24;

  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private UserService userService;

  @Override
  protected void fetch() {
    try {
      List<User> users = githubApi.fetchAllOrgaMembers();
      users.forEach(this::handleUser);
    } catch (IOException e) {
      logger.error("Error fetching users from GitHub: " + e.getMessage(), e);
    }
  }

  private void handleUser(User user) {
    logger.debug("fetched user: " + user.username);
    user.canLogin = true;
    userService.insertOrUpdate(user);
  }

  /**
   * Schedule execution between 01:00 AM o'clock and 02:00 AM o'clock.
   *
   * @return
   */
  @Override
  protected Date nextFetch() {
    int diff = HOURS_OF_DAY - LocalTime.now().getHour();
    logger.debug("Still " + diff + " hours until midnight.");
    diff++;
    return Date.from(Instant.now().plus(diff, ChronoUnit.HOURS));
  }
}

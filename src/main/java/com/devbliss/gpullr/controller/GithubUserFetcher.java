package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.service.github.GithubApi;
import com.devbliss.gpullr.util.Log;
import java.io.IOException;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Fetches all users belonging to Devbliss from GitHub API.
 */
@Component
public class GithubUserFetcher {

  private static final int HOURS_OF_DAY = 24;

  @Log
  private Logger logger;

  @Autowired
  private GithubApi githubApi;

  @Autowired
  private UserService userService;

  private ThreadPoolTaskScheduler executor;

  public GithubUserFetcher() {
    executor = new ThreadPoolTaskScheduler();
    executor.initialize();
  }

  public void fetchUsers() {
    try {
      logger.info("Start fetching users from GitHub...");
      List<User> users = githubApi.fetchAllOrgaMembers();
      users.forEach(this::handleUser);
      logger.info("Finished fetching users from GitHub,");

      executor.schedule(() -> fetchUsers(), calculateNextUserFetch());
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
  private Date calculateNextUserFetch() {
    int diff = HOURS_OF_DAY - Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    logger.debug("Still " + diff + " hours until midnight.");
    diff++;
    Date nextExecution = Date.from(Instant.now().plusSeconds(diff * 3600));
    logger.debug("The next fetch of organization members from github will be at: "
        + DateFormat.getInstance().format(nextExecution));

    return nextExecution;
  }

}

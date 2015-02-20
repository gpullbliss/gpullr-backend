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
      // List<User> users = githubApi.fetchAllOrgaMembers();
      List<User> users = temporaryUsersList();
      users.forEach(this::handleUser);
      logger.info("Finished fetching users from GitHub,");

      // executor.schedule(() -> fetchUsers(), calculateNextUserFetch());
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

  private List<User> temporaryUsersList() throws IOException {
    List<User> userList = Arrays.asList(
        new User(496860, "bastien03", "https://avatars.githubusercontent.com/u/496860?v=3", true),
        new User(6431151, "bg-devbliss", "https://avatars.githubusercontent.com/u/6431151?v=3", true),
        new User(986254, "bickralf", "https://avatars.githubusercontent.com/u/986254?v=3", true),
        new User(1777758, "chrisulrich", "https://avatars.githubusercontent.com/u/1777758?v=3", true),
        new User(1681370, "cthies", "https://avatars.githubusercontent.com/u/1681370?v=3", true),
        new User(2528109, "devbliss-iNes", "https://avatars.githubusercontent.com/u/2528109?v=3", true),
        new User(3973165, "devbliss-jenkins", "https://avatars.githubusercontent.com/u/3973165?v=3", true),
        new User(2890982, "devbliss-pullonly", "https://avatars.githubusercontent.com/u/2890982?v=3", true),
        new User(885985, "dmitry-vinogradov", "https://avatars.githubusercontent.com/u/885985?v=3", true),
        new User(7847193, "doernbrackandre", "https://avatars.githubusercontent.com/u/7847193?v=3", true),
        new User(1567631, "domjansen", "https://avatars.githubusercontent.com/u/1567631?v=3", true),
        new User(125736, "dtamberg", "https://avatars.githubusercontent.com/u/125736?v=3", true),
        new User(1488040, "dwalldorf", "https://avatars.githubusercontent.com/u/1488040?v=3", true),
        new User(1362436, "EnricoSchw", "https://avatars.githubusercontent.com/u/1362436?v=3", true),
        new User(6554430, "frederithub", "https://avatars.githubusercontent.com/u/6554430?v=3", true),
        new User(10921361, "gpullr-backend", "https://avatars.githubusercontent.com/u/10921361?v=3", true),
        new User(1777316, "Hillkorn", "https://avatars.githubusercontent.com/u/1777316?v=3", true),
        new User(95374, "jopek", "https://avatars.githubusercontent.com/u/95374?v=3", true),
        new User(1898347, "karstenzimmer", "https://avatars.githubusercontent.com/u/1898347?v=3", true),
        new User(1202182, "Kifah", "https://avatars.githubusercontent.com/u/1202182?v=3", true),
        new User(1460875, "laGenteEstaMuyLoca81", "https://avatars.githubusercontent.com/u/1460875?v=3", true),
        new User(1871254, "lquintela", "https://avatars.githubusercontent.com/u/1871254?v=3", true),
        new User(308374, "marcelb", "https://avatars.githubusercontent.com/u/308374?v=3", true),
        new User(513777, "marcusschroeder", "https://avatars.githubusercontent.com/u/513777?v=3", true),
        new User(7857307, "masmu", "https://avatars.githubusercontent.com/u/7857307?v=3", true),
        new User(971117, "matthias-walter", "https://avatars.githubusercontent.com/u/971117?v=3", true),
        new User(8616698, "mdio", "https://avatars.githubusercontent.com/u/8616698?v=3", true),
        new User(364199, "nbari", "https://avatars.githubusercontent.com/u/364199?v=3", true),
        new User(3127128, "okarahan", "https://avatars.githubusercontent.com/u/3127128?v=3", true),
        new User(8035233, "p0tgn0m", "https://avatars.githubusercontent.com/u/8035233?v=3", true),
        new User(2143421, "pkarstedtDevbliss", "https://avatars.githubusercontent.com/u/2143421?v=3", true),
        new User(3671239, "ptc0x", "https://avatars.githubusercontent.com/u/3671239?v=3", true),
        new User(446994, "retrostyle", "https://avatars.githubusercontent.com/u/446994?v=3", true),
        new User(1440995, "roughy", "https://avatars.githubusercontent.com/u/1440995?v=3", true),
        new User(8223211, "rueckmannDevB", "https://avatars.githubusercontent.com/u/8223211?v=3", true),
        new User(458321, "sfstpala", "https://avatars.githubusercontent.com/u/458321?v=3", true),
        new User(1777303, "shafel", "https://avatars.githubusercontent.com/u/1777303?v=3", true),
        new User(793373, "tobiinc", "https://avatars.githubusercontent.com/u/793373?v=3", true),
        new User(1777918, "tumminelli", "https://avatars.githubusercontent.com/u/1777918?v=3", true),
        new User(266249, "ujanssen", "https://avatars.githubusercontent.com/u/266249?v=3", true),
        new User(668408, "wolfgangpfnuer", "https://avatars.githubusercontent.com/u/668408?v=3", true)
    );
    return userList;
  }
}

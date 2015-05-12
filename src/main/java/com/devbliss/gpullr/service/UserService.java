package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.exception.BadRequestException;
import com.devbliss.gpullr.exception.LoginRequiredException;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.session.UserSession;
import com.devbliss.gpullr.util.Constants;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business Layer for {@link com.devbliss.gpullr.domain.User} objects.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class UserService {

  private final UserRepository userRepository;

  private UserSession userSession;

  @Autowired
  public UserService(UserRepository userRepository, UserSession userSession) {
    this.userRepository = userRepository;
    this.userSession = userSession;
  }

  public void insertOrUpdate(User user) {
    // don't override user settings, if user already exists
    if (user.id != null && user.userSettings == null) {
      User dbUser = userRepository.findOne(user.id);
      if (dbUser != null && dbUser.userSettings != null) {
        user.userSettings = dbUser.userSettings;
      }
    } else if (user.userSettings == null) {
      user.userSettings = new UserSettings();
      user.userSettings.language = Constants.DEFAULT_LANGUAGE;
    }

    userRepository.save(user);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public Optional<User> findById(Integer userId) {
    return userRepository.findById(userId);
  }

  public void requireLogin() throws LoginRequiredException {
    if (userSession.getUser() == null) {
      throw new LoginRequiredException();
    }
  }

  public void login(int id) {
    User loggedInUser = userRepository.findOne(id);
    if (loggedInUser == null || !loggedInUser.canLogin) {
      throw new BadRequestException("Login data invalid");
    }

    userSession.setUser(loggedInUser);
  }

  public User whoAmI() throws LoginRequiredException {
    requireLogin();
    return userSession.getUser();
  }

  public Optional<User> getCurrentUserIfLoggedIn() {
    return Optional.ofNullable(userSession.getUser());
  }

  public User updateUserSettings(int userId, UserSettings update) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(
            () -> new NotFoundException("Cannot update user settings for non-existing user with id " + userId));

    if (user.userSettings != null) {
      // update existing user settings
      user.userSettings.assignedPullRequestsOrdering = update.assignedPullRequestsOrdering;
      user.userSettings.unassignedPullRequestsOrdering = update.unassignedPullRequestsOrdering;
      user.userSettings.repoBlackList = update.repoBlackList;
      user.userSettings.language = update.language;
    } else {
      user.userSettings = update;
    }

    insertOrUpdate(user);
    return user;
  }

  public void updateUserSession(User user) {
    userSession.setUser(user);
  }
}

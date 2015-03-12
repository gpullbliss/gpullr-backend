package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.exception.LoginRequiredException;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.session.UserSession;
import java.util.List;
import java.util.stream.Collectors;
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
    }

    userRepository.save(user);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User findOne(Integer userId) {
    return userRepository.findOne(userId);
  }

  /**
   * Finds all users that are allowed to login to this application, sorted by username.
   *
   * @return possibly empty list of users
   */
  public List<User> findAllOrgaMembers() {
    return userRepository
      .findByCanLoginIsTrue()
      .stream()
      .sorted((u1, u2) -> u1.username.toLowerCase().compareTo(u2.username.toLowerCase()))
      .collect(Collectors.toList());
  }

  public void requireLogin() throws LoginRequiredException {
    if (userSession.getUser() == null) {
      throw new LoginRequiredException();
    }
  }

  public void login(int id) {
    User loggedInUser = userRepository.findOne(id);
    userSession.setUser(loggedInUser);
  }

  public void updateUserSession(User user) {
    userSession.setUser(user);
  }

  public User whoAmI() throws LoginRequiredException {
    requireLogin();
    return userSession.getUser();
  }
}

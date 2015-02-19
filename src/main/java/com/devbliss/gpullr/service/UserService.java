package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.session.UserSession;
import java.util.ArrayList;
import java.util.List;
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

  private List<User> devblissMembers;

  @Autowired
  public UserService(UserRepository userRepository, UserSession userSession) {
    this.userRepository = userRepository;
    this.userSession = userSession;
    this.devblissMembers = new ArrayList();
  }

  public void insertOrUpdate(User user) {
    userRepository.save(user);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public List<User> findAllOrgaMembers() {
    devblissMembers.clear();
    userRepository.findAll().forEach(user -> addDevblissMember(user));
    return devblissMembers;
  }

  public void login(int id) {
    User loggedInUser = userRepository.findOne(id);
    userSession.setUser(loggedInUser);
  }

  public User whoAmI() {
    if (userSession.getUser() != null) {
      return userSession.getUser();
    } else {
      return null;
    }
  }

  private void addDevblissMember(User user) {
    if (user.canLogin) {
      devblissMembers.add(user);
    }
  }
}

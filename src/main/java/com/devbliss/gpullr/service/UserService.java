package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Business Layer for {@link com.devbliss.gpullr.domain.User} objects.
 */
@Component
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public void save(User user) {
    userRepository.save(user);
  }

}

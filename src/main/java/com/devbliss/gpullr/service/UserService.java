package com.devbliss.gpullr.service;

import com.devbliss.gpullr.controller.dto.UserDto;
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

  public User toEntity(UserDto dto) {
    // TODO: implement
    return new User();
  }

  public void save(UserDto userDto) {
    // TODO: convert to entity

    userRepository.save(toEntity(userDto));
  }

}

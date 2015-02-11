package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.User;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link com.devbliss.gpullr.domain.User} and {@link com.devbliss.gpullr.controller.dto.UserDto} objects.
 */
@Component
public class UserConverter {

  public UserDto toDto(User entity) {
    // TODO: implement
    return null;
  }

  public User toEntity(UserDto dto) {
    User user = new User();
    user.id = dto.id;
    user.username = dto.username;
    user.fullname = dto.fullname;
    user.externalUserId = dto.externalUserId;
    user.avatarUrl = dto.avatarUrl;
    user.token = dto.token;

    return user;
  }

}

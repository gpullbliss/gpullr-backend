package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.User;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link User} and {@link UserDto} objects.
 *
 */
@Component
public class UserConverter {

  public UserDto toDto(User entity) {
    UserDto userDto = new UserDto();
    userDto.id = entity.id;
    userDto.username = entity.username;
    userDto.avatarUrl = entity.avatarUrl;

    return userDto;
  }
}

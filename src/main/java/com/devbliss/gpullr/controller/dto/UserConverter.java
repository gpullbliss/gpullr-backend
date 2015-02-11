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
    user.setId(dto.getId());
    user.setUsername(dto.getUsername());
    user.setFullname(dto.getFullname());
    user.setExternalUserId(dto.getExternalUserId());
    user.setAvatarUrl(dto.getAvatarUrl());
    user.setToken(dto.getToken());

    return user;
  }

}

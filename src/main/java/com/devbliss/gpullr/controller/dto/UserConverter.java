package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link User} and {@link UserDto} objects.
 */
@Component
public class UserConverter {

  @Autowired
  private UserSettingsConverter userSettingsConverter;

  public MinimalUserDto toMinimalDto(User entity) {
    MinimalUserDto dto = new MinimalUserDto();
    dto.id = entity.id;
    dto.username = entity.username;
    dto.avatarUrl = entity.avatarUrl;
    dto.fullName = entity.fullName;
    dto.profileUrl = entity.profileUrl;
    return dto;
  }

  public UserDto toDto(User entity) {

    UserDto dto = new UserDto();
    dto.id = entity.id;
    dto.username = entity.username;
    dto.avatarUrl = entity.avatarUrl;
    dto.profileUrl = entity.profileUrl;
    dto.fullName = entity.fullName;

    if (entity.userSettings != null) {
      dto.userSettingsDto = userSettingsConverter.toDto(entity.userSettings);
    }

    return dto;
  }
}

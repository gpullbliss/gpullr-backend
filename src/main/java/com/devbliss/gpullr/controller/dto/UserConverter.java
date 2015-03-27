package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.MinimalUser;
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

  public MinimalUserDto toMinimalDto(MinimalUser entity) {
    MinimalUserDto dto = new MinimalUserDto();
    dto.username = entity.username;
    dto.avatarUrl = entity.avatarUrl;
    return dto;
  }

  public MinimalUserDto toMinimalDto(User entity) {
    MinimalUserDto dto = new MinimalUserDto();
    dto.id = entity.id;
    dto.username = entity.username;
    dto.avatarUrl = entity.avatarUrl;
    return dto;
  }

  public UserDto toDto(User entity) {
    UserDto dto = toDto(entity);

    if (entity.userSettings != null) {
      dto.userSettingsDto = userSettingsConverter.toDto(entity.userSettings);
    }

    return dto;
  }
}

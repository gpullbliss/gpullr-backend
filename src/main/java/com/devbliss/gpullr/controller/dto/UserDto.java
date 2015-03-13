package com.devbliss.gpullr.controller.dto;

/**
 * DTO for JSON de-/serialization of {@link com.devbliss.gpullr.domain.User} instances.
 */
public class UserDto {

  public int id;

  public String username;

  public String avatarUrl;

  public UserSettingsDto userSettingsDto;

}

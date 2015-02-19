package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.User;

/**
 * DTO for JSON de-/serialization of {@link User} instances.
 *
 */
public class UserDto {
  public int id;
  public String username;
  public String avatarUrl;
}

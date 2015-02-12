package com.devbliss.gpullr.controller.dto;

/**
 * DTO for {@link com.devbliss.gpullr.domain.User} object.
 */
public class UserDto {

  public Long id;

  public String username;

  public String fullname;

  // user id at GitHub
  public String externalUserId;

  public String avatarUrl;

  public String token;

}

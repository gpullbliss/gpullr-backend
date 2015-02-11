package com.devbliss.gpullr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User of the application
 */
@Entity
public class User {

  @Id
  @GeneratedValue
  public  Long id;

  @NotBlank
  @Column(unique = true)
  public String username;

  @NotBlank
  public String fullname;

  // user id at GitHub
  @NotBlank
  public String externalUserId;

  public String avatarUrl;

  @NotBlank
  public String token;

}
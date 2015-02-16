package com.devbliss.gpullr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User of the application, fetched from GitHub API.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Entity
public class User {

  @Id
  public Integer id;

  @NotBlank
  @Column(unique = true)
  public String username;

  public String avatarUrl;


  public User() {
  }

  public User(Integer id, String username, String avatarUrl) {
    this.id = id;
    this.username = username;
    this.avatarUrl = avatarUrl;
  }
}
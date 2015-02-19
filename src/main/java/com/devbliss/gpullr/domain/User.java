package com.devbliss.gpullr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User of the application, fetched from GitHub API.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Entity
public class User {

  @Id
  @NotNull
  public Integer id;

  @NotBlank
  @Column(unique = true)
  public String username;

  public String avatarUrl;

  public boolean canLogin;

  public User() {
  }

  public User(Integer id, String username, String avatarUrl) {
    this.id = id;
    this.username = username;
    this.avatarUrl = avatarUrl;
    this.canLogin = false;
  }

  public User(Integer id, String username, String avatarUrl, boolean canLogin) {
    this.id = id;
    this.username = username;
    this.avatarUrl = avatarUrl;
    this.canLogin = canLogin;
    this.canLogin = false;
  }

  public String toConstructorString() {
    return String.format("new User(%d, \"%s\", \"%s\", %s)", id, username, avatarUrl, canLogin);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", avatarUrl='" + avatarUrl + '\'' +
        ", canLogin=" + canLogin +
        '}';
  }
}

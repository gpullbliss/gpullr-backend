package com.devbliss.gpullr.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

  public String fullName;

  public String avatarUrl;

  public String profileUrl;

  public Boolean canLogin = false;

  @OneToOne(
      fetch = FetchType.EAGER,
      targetEntity = UserSettings.class,
      cascade = CascadeType.PERSIST,
      orphanRemoval = true)
  @JoinColumn(name = "user_settings_id")
  public UserSettings userSettings;

  public User() {}

  public User(Integer id, String username) {
    this(id, username, null, null, false, null, null);
  }

  public User(Integer id, String username, String fullName, String avatarUrl, String profileUrl) {
    this(id, username, fullName, avatarUrl, false, profileUrl, null);
  }

  public User(Integer id, String username, String fullName, String avatarUrl, Boolean canLogin,
      String profileUrl, UserSettings userSettings) {
    this.id = id;
    this.username = username;
    this.fullName = fullName;
    this.avatarUrl = avatarUrl;
    this.canLogin = canLogin;
    this.profileUrl = profileUrl;
    this.userSettings = userSettings;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", fullName='" + fullName + '\'' +
        ", avatarUrl='" + avatarUrl + '\'' +
        ", canLogin=" + canLogin +
        '}';
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    User other = (User) obj;

    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (username == null) {
      if (other.username != null) {
        return false;
      }
    } else if (!username.equals(other.username)) {
      return false;
    }

    return true;
  }

}

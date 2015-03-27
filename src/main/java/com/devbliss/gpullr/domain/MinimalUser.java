package com.devbliss.gpullr.domain;

import javax.persistence.Embeddable;

/**
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Embeddable
public class MinimalUser {

  MinimalUser() {}

  public MinimalUser(String username, String avatarUrl) {
    this.username = username;
    this.avatarUrl = avatarUrl;
  }

  public String username;

  public String avatarUrl;
}

package com.devbliss.gpullr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Individual settings for a user of the application.
 */
@Entity
public class UserSettings {

  public enum OrderOption {
    ASC,
    DESC
  }

  @Id
  @Column(name = "user_settings_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @Column
  @Enumerated(value = EnumType.STRING)
  public OrderOption defaultPullRequestListOrdering;

  public UserSettings() {
  }

  public UserSettings(OrderOption ordering) {
    this.defaultPullRequestListOrdering = ordering;
  }
}

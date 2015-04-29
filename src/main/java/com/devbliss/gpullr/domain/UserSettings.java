package com.devbliss.gpullr.domain;

import static com.devbliss.gpullr.util.Constants.ALLOWED_LANGUAGES;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.AssertTrue;

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

  @Column(name = "UNASSIGNED_PULLREQUESTS_ORDERING")
  @Enumerated(value = EnumType.STRING)
  public OrderOption unassignedPullRequestsOrdering;
  
  @Column(name = "ASSIGNED_PULLREQUESTS_ORDERING")
  @Enumerated(value = EnumType.STRING)
  public OrderOption assignedPullRequestsOrdering;

  @ElementCollection(fetch = FetchType.EAGER)
  public List<Integer> repoBlackList = new ArrayList<>();

  public String language;

  public UserSettings() {}

  public UserSettings(OrderOption unassignedPullRequestsOrdering) {
    this.unassignedPullRequestsOrdering = unassignedPullRequestsOrdering;
  }

  public UserSettings(OrderOption unassignedPullRequestsOrdering, List<Integer> repoBlackList) {
    this.unassignedPullRequestsOrdering = unassignedPullRequestsOrdering;
    this.repoBlackList = repoBlackList;
  }

  public UserSettings(OrderOption defaultPullRequestListOrdering, List<Integer> repoBlackList, String language) {
    this(defaultPullRequestListOrdering, repoBlackList);
    this.language = language;
  }

  @AssertTrue
  public boolean isLocaleValid() {
    return language == null || ALLOWED_LANGUAGES.containsKey(language);
  }
}

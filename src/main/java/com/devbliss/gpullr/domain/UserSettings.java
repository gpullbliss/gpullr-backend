package com.devbliss.gpullr.domain;

import javax.validation.constraints.Size;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Individual settings for a user of the application.
 */
@Entity
public class UserSettings {

  public static final List<String> ALLOWED_LANGUAGES = Arrays.asList(
      Locale.GERMAN.getLanguage(),
      Locale.ENGLISH.getLanguage());

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

  @ElementCollection(fetch = FetchType.EAGER)
  public List<Integer> repoBlackList = new ArrayList<>();

  @Size(min=2, max=2)
  public String language;

  public UserSettings() {}

  public UserSettings(OrderOption ordering) {
    this.defaultPullRequestListOrdering = ordering;
  }

  public UserSettings(OrderOption defaultPullRequestListOrdering, List<Integer> repoBlackList) {
    this.defaultPullRequestListOrdering = defaultPullRequestListOrdering;
    this.repoBlackList = repoBlackList;
  }
  
  public UserSettings(OrderOption defaultPullRequestListOrdering, List<Integer> repoBlackList, String language) {
    this(defaultPullRequestListOrdering, repoBlackList);
    this.language = language;
  }
  
  @AssertTrue
  public boolean isLocaleValid() {
    return true;
  }
}

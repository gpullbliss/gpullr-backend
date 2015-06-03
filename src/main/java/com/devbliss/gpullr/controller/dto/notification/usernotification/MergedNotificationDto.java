package com.devbliss.gpullr.controller.dto.notification.usernotification;

import com.devbliss.gpullr.domain.notifications.UserNotificationType;

/**
 * Notification for merged pull requests.
 */
public class MergedNotificationDto implements UserNotificationDto {

  private long id;

  private String createdAt;

  private String actorName;

  private String repoTitle;

  private String pullRequestTitle;

  private String userNotificationType = UserNotificationType.PULLREQUEST_CLOSED;

  @Override
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getActorName() {
    return actorName;
  }

  public void setActorName(String actorName) {
    this.actorName = actorName;
  }

  public String getRepoTitle() {
    return repoTitle;
  }

  public void setRepoTitle(String repoTitle) {
    this.repoTitle = repoTitle;
  }

  public String getPullRequestTitle() {
    return pullRequestTitle;
  }

  public void setPullRequestTitle(String pullRequestTitle) {
    this.pullRequestTitle = pullRequestTitle;
  }

  public String getType() {
    return userNotificationType;
  }

}

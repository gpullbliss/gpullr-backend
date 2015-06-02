package com.devbliss.gpullr.controller.dto.notification.usernotification;

import com.devbliss.gpullr.domain.notifications.UserNotificationType;

/**
 * Notification for new comments.
 */
public class CommentNotificationDto implements UserNotificationDto {

  private long id;

  private String createdAt;

  private String repoTitle;

  private String pullRequestTitle;

  private long commentCount;

  private UserNotificationType type = UserNotificationType.COMMENT;

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

  public long getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(long commentCount) {
    this.commentCount = commentCount;
  }

  @Override
  public UserNotificationType getType() {
    return type;
  }
}

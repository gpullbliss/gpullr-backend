package com.devbliss.gpullr.domain;

import javax.persistence.CascadeType;

import com.devbliss.gpullr.domain.notifications.PullRequestCommentedUserNotification;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment {

  @Id
  private String id;

  private String diffHunk;

  private ZonedDateTime createdAt;

  @ManyToOne(optional = false)
  private PullRequest pullRequest;

  @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
  @JoinTable(
      name = "NOTIFICATION_COMMENTS",
      joinColumns = @JoinColumn(name = "COMMENT_ID"),
      inverseJoinColumns = @JoinColumn(name = "NOTIFICATION_ID"))
  public List<PullRequestCommentedUserNotification> notifications;

  public String getDiffHunk() {
    return diffHunk;
  }

  public void setDiffHunk(String diffHunk) {
    this.diffHunk = diffHunk;
  }

  public PullRequest getPullRequest() {
    return pullRequest;
  }

  public void setPullRequest(PullRequest pullRequest) {
    this.pullRequest = pullRequest;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }
}

package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comments")
public class PullRequestComment {

  @Id
  private String id;

  private String diffHunk;

  private ZonedDateTime createdAt;

  @ManyToOne(optional = false)
  private PullRequest pullRequest;

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

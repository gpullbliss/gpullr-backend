package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;

public class PullRequestComment {

  private String url;

  private int id;

  private String commitId;

  private String originalCommitId;

  private int position;

  private int originalPosition;

  private String body;

  private ZonedDateTime createdAt;

  private ZonedDateTime updatedAt;

  private String pullRequestUrl;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCommitId() {
    return commitId;
  }

  public void setCommitId(String commitId) {
    this.commitId = commitId;
  }

  public String getOriginalCommitId() {
    return originalCommitId;
  }

  public void setOriginalCommitId(String originalCommitId) {
    this.originalCommitId = originalCommitId;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int getOriginalPosition() {
    return originalPosition;
  }

  public void setOriginalPosition(int originalPosition) {
    this.originalPosition = originalPosition;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ZonedDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(ZonedDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getPullRequestUrl() {
    return pullRequestUrl;
  }

  public void setPullRequestUrl(String pullRequestUrl) {
    this.pullRequestUrl = pullRequestUrl;
  }
}

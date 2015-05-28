package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;

public class PullRequestComment {

  public String url;

  public int id;

  public String commitId;

  public String originalCommitId;

  public int position;

  public int originalPosition;

  public String body;

  public ZonedDateTime createdAt;

  public ZonedDateTime updatedAt;

  public String pullRequestUrl;
}

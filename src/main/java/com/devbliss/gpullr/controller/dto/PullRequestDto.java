package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.User;

public class PullRequestDto {

  public Integer id;

  public String title;

  public String url;

  public String repoName;

  public User author;

  public String createdAt;

  public Integer filesChanged;

  public Integer linesAdded;

  public Integer linesRemoved;

  public String status;

  public User assignee;

  public Integer number;

  public String assignedAt;
}

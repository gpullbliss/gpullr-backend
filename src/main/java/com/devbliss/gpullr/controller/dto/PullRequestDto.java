package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.User;

public class PullRequestDto {

  public Integer id;
  
  public String title;
  
  public String url;
  
  public String repository;
  
  public User author;
  
  public String creationDate;
  
  public Integer filesChanged;
  
  public Integer linesAdded;
  
  public Integer linesRemoved;
  
  public String status;
  
  public User assignee;
}

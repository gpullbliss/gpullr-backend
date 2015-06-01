package com.devbliss.gpullr.domain;

import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class Commit {

  @Id
  public String sha;

  @ManyToOne(optional = false)
  public PullRequest pullRequest;

  public Commit() {

  }

  public Commit(String sha) {
    this.sha = sha;
  }
  
  public Commit(String sha, PullRequest pullRequest) {
    this.sha = sha;
    this.pullRequest = pullRequest;
  }
}

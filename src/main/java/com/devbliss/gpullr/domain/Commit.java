package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;

import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class Commit {

  @Id
  public String sha;

  @ManyToOne(optional = false)
  public PullRequest pullRequest;
  
  public ZonedDateTime commitDate;

  protected Commit() {
    // for JPA
  }

  public Commit(String sha, ZonedDateTime commitDate) {
    this.sha = sha;
    this.commitDate = commitDate;
  }
}

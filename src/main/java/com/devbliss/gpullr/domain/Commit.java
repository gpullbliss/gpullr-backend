package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Represents a commit belonging to a pull request. Relevant for generating notifications for pull
 * request authors and reviewers. 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Entity
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

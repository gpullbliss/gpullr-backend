package com.devbliss.gpullr.domain;

import javax.persistence.ManyToOne;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class Pullrequest {

  public enum State {
    OPEN
  }

  @Id
  @NotNull
  @Min(1)
  public Integer id;

  @ManyToOne(optional = false)
  public GithubRepo repo;

  public String url;

  public ZonedDateTime createdAt;

  public State state;

  public Integer additions;

  public Integer deletions;

  public Integer changedFiles;

}

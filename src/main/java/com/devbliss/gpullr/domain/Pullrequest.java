package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class Pullrequest {

  public enum State {
    OPEN, MERGED
  }

  @Id
  @NotNull
  @Min(1)
  public Integer id;

  @ManyToOne(optional = false)
  public Repo repo;

  public String url;

  public ZonedDateTime createdAt;

  public State state;

  public User author;

  public Integer additions;

  public Integer deletions;

  public Integer changedFiles;

}

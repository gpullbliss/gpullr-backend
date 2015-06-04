package com.devbliss.gpullr.domain;

public class PullRequestCommentEvent extends Event {

  public final Comment comment;

  public PullRequestCommentEvent(Comment comment) {
    this.comment = comment;
  }

}

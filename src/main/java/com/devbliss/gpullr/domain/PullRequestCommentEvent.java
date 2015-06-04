package com.devbliss.gpullr.domain;

public class PullRequestCommentEvent extends Event {

  public Comment comment;

  public PullRequestCommentEvent(Comment comment) {
    this.comment = comment;
  }

}

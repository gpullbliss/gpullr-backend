package com.devbliss.gpullr.domain;

public class PullRequestCommentEvent extends Event {

  public PullRequestComment comment;

  public PullRequestCommentEvent(PullRequestComment comment) {
    this.comment = comment;
  }

}

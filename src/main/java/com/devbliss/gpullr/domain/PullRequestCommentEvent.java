package com.devbliss.gpullr.domain;

public class PullRequestCommentEvent extends Event {

  public final Comment comment;

  public final int pullRequestId;

  public PullRequestCommentEvent(Comment comment, int pullRequestId) {
    this.comment = comment;
    this.pullRequestId = pullRequestId;
  }

}

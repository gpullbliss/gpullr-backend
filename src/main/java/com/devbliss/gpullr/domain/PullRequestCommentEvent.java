package com.devbliss.gpullr.domain;

public class PullRequestCommentEvent extends Event {

  public final Comment comment;

  public int pullRequestId;

  public String pullRequestUrl;

  public PullRequestCommentEvent(Comment comment, String pullRequestUrl) {
    this.comment = comment;
    this.pullRequestUrl = pullRequestUrl;
  }

  public PullRequestCommentEvent(Comment comment, int pullRequestId) {
    this.comment = comment;
    this.pullRequestId = pullRequestId;
    pullRequestUrl = "";
  }

  public boolean hasPullRequestUrl() {
    return !pullRequestUrl.isEmpty();
  }

  @Override
  public String toString() {
    return "PullRequestCommentEvent{" +
        "comment=" + comment +
        ", pullRequestId=" + pullRequestId +
        ", pullRequestUrl='" + pullRequestUrl + '\'' +
        "} " + super.toString();
  }
}

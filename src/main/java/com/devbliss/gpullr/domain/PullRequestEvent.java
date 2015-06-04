package com.devbliss.gpullr.domain;


/**
 * Event referring to a pullRequest as received from GitHub API. Contains the pull request itself and the type of
 * event, e.g. <code>PULLREQUEST_CREATED</code>.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public class PullRequestEvent extends Event {

  public final Action action;
  public final PullRequest pullRequest;

  public enum Action {
    ASSIGNED, UNASSIGNED, LABELED, UNLABELED, OPENED, CLOSED, REOPENED, SYNCHRONIZE;

    public static Action parse(String lowerCaseName) {
      return valueOf(lowerCaseName.toUpperCase());
    }
  }

  public PullRequestEvent(Action action, PullRequest pullRequest) {
    this.action = action;
    this.pullRequest = pullRequest;
  }
}

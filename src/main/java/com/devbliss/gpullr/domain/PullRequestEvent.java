package com.devbliss.gpullr.domain;

/**
 * Event referring to a pullrequest as received from GitHub API. Contains the pull request itself and the type of
 * event, e.g. <code>PULLREQUEST_CREATED</code>.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public class PullRequestEvent {

  public enum Action {
    ASSIGNED, UNASSIGNED, LABELED, UNLABELED, OPENED, CLOSED, REOPENED, SYNCHRONIZE;
    
    public static Action parse(String lowerCaseName) {
      return valueOf(lowerCaseName.toUpperCase());
    }
  }

  public final Action action;

  public final PullRequest pullrequest;

  public PullRequestEvent(Action action, PullRequest pullrequest) {
    this.action = action;
    this.pullrequest = pullrequest;
  }
}

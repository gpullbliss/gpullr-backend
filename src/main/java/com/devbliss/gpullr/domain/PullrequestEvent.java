package com.devbliss.gpullr.domain;

/**
 * Event referring to a pullrequest as received from GitHub API. Contains the pull request itself and the type of
 * event, e.g. <code>PULLREQUEST_CREATED</code>.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public class PullrequestEvent {

  public enum Type {
    PULLREQUEST_CREATED, PULLREQUEST_CLOSED
  }

  public final Type type;

  public final Pullrequest pullrequest;

  public PullrequestEvent(Type type, Pullrequest pullrequest) {
    this.type = type;
    this.pullrequest = pullrequest;
  }
}

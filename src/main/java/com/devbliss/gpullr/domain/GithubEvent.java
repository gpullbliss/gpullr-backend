package com.devbliss.gpullr.domain;

/**
 * An event retrieved from GitHub API. Not meant to be stored directly in our persistence layer but being further
 * processed instead.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public abstract class GithubEvent<PAYLOAD> {
  
  public enum Type {
    PULLREQUEST_CREATED
  }
  
  public final Type type;
  
  public PAYLOAD payload;
  
  protected GithubEvent(Type type) {
    this.type = type;
  }
}

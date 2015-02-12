package com.devbliss.gpullr.domain;


public class PullrequestEvent {

  public enum Type {
    PULLREQUEST_CREATED
  }
  
  public final Type type;
  
  public final Pullrequest pullrequest;

  public  PullrequestEvent(Type type, Pullrequest pullrequest) {
    this.type = type;
    this.pullrequest = pullrequest;
  }
}

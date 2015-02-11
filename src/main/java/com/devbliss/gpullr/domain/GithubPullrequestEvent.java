package com.devbliss.gpullr.domain;

public class GithubPullrequestEvent extends GithubEvent<Pullrequest> {

  public final Pullrequest pullrequest;

  public  GithubPullrequestEvent(Type type, Pullrequest pullrequest) {
    super(type);
    this.pullrequest = pullrequest;
  }

}

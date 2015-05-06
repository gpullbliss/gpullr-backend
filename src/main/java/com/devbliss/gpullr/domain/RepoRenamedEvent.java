package com.devbliss.gpullr.domain;

import org.springframework.context.ApplicationEvent;

/**
 * To be fired when a repo has been renamed, which is detected when refreshing the list of active repos from 
 * GitHub. 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class RepoRenamedEvent extends ApplicationEvent {

  private static final long serialVersionUID = 2838596932912166348L;

  public final Repo renamedRepo;

  public RepoRenamedEvent(Object source, Repo renamedRepo) {
    super(source);
    this.renamedRepo = renamedRepo;
  }

}
